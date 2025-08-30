#!/bin/bash

# =============================================================================
# Lambda Function Build and Upload Script
# =============================================================================
# This script packages Python Lambda functions and uploads them to S3
# Author: Financial Pulse Development Team
# =============================================================================

# Enable strict error handling
set -euo pipefail  # -e: exit on error, -u: error on undefined variables, -o pipefail: pipe failures cause exit

# =============================================================================
# Configuration and Defaults
# =============================================================================

# Color codes for output formatting
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
DEFAULT_OUTPUT_DIR="."
DEFAULT_S3_BUCKET="fp-lambda-deployments-dev-us-east-1"
DOCKER_IMAGE_NAME="lambda-packer"

# =============================================================================
# Helper Functions
# =============================================================================

# Print colored messages
print_error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

# Show usage instructions
show_help() {
    cat << EOF
Usage: $(basename "$0") [OPTIONS]

Package Python Lambda functions and optionally upload to S3.

OPTIONS:
    -p, --python-dir PATH      Directory containing Python files and requirements.txt (required)
    -o, --output-dir PATH      Output directory for the zip file (default: current directory)
    -s, --s3-bucket NAME       S3 bucket for upload (default: $DEFAULT_S3_BUCKET)
    -n, --no-upload           Skip S3 upload step
    -c, --clean               Clean Docker image after use
    -h, --help                Show this help message

EXAMPLES:
    # Package Lambda function from current directory
    $(basename "$0") -p .

    # Package and specify output directory
    $(basename "$0") -p ./my-lambda -o ./dist

    # Package without uploading to S3
    $(basename "$0") -p ./my-lambda -n

    # Package, upload to custom S3 bucket, and clean Docker image
    $(basename "$0") -p ./my-lambda -s my-custom-bucket -c

REQUIREMENTS:
    - Docker must be installed and running
    - AWS CLI must be configured (if uploading to S3)
    - requirements.txt must exist in the Python directory

EOF
}

# Validate directory exists and contains requirements.txt
validate_python_dir() {
    local dir="$1"
    
    if [[ ! -d "$dir" ]]; then
        print_error "Directory does not exist: $dir"
        return 1
    fi
    
    if [[ ! -f "$dir/requirements.txt" ]]; then
        print_error "requirements.txt not found in: $dir"
        return 1
    fi
    
    # Check if there are any Python files
    if ! ls "$dir"/*.py >/dev/null 2>&1; then
        print_error "No Python files found in: $dir"
        return 1
    fi
    
    return 0
}

# Build Docker image for Lambda packaging
build_docker_image() {
    print_info "Checking Docker image: $DOCKER_IMAGE_NAME"
    
    # Check if Docker is running
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    
    # Check if image exists
    if docker images -q "$DOCKER_IMAGE_NAME" 2>/dev/null | grep -q .; then
        print_info "Docker image already exists"
    else
        print_info "Building Docker image..."
        
        # Get script directory for Dockerfile
        local script_dir
        script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
        
        # Build the image
        if docker build -t "$DOCKER_IMAGE_NAME" "$script_dir" >/dev/null 2>&1; then
            print_success "Docker image built successfully"
        else
            print_error "Failed to build Docker image"
            exit 1
        fi
    fi
}

# Package Lambda function using Docker
zip_file=""
package_lambda() {
    local python_dir="$1"
    local output_dir="$2"
    
    # Generate output filename based on directory name
    local dir_name
    dir_name="$(basename "$python_dir")"
    local zip_name="${dir_name}.zip"
    local zip_path="${output_dir}/${zip_name}"
    
    print_info "Packaging Lambda function: $dir_name"
    print_info "Source: $python_dir"
    print_info "Output: $zip_path"
    
    # Remove existing zip if it exists
    if [[ -f "$zip_path" ]]; then
        print_info "Removing existing zip file"
        rm -f "$zip_path"
    fi
    
    # Create packaging script content
    local package_script
    package_script=$(cat << 'SCRIPT'
#!/bin/bash
set -e

PYTHON_DIR="$1"
OUTPUT_ZIP="$2"
TEMP_DIR=$(mktemp -d)

echo "Installing dependencies..."
if [[ -s "$PYTHON_DIR/requirements.txt" ]]; then
    pip install -r "$PYTHON_DIR/requirements.txt" -t "$TEMP_DIR" --quiet
fi

echo "Copying Python files..."
cp -r "$PYTHON_DIR"/*.py "$TEMP_DIR/" 2>/dev/null || true

# Copy any subdirectories with Python files
for dir in "$PYTHON_DIR"/*/; do
    if [[ -d "$dir" ]] && ls "$dir"/*.py >/dev/null 2>&1; then
        dir_name=$(basename "$dir")
        cp -r "$dir" "$TEMP_DIR/$dir_name"
    fi
done

echo "Creating zip archive..."
cd "$TEMP_DIR"
7z a "$OUTPUT_ZIP" . -tzip -mx=5 >/dev/null 2>&1

rm -rf "$TEMP_DIR"
echo "Package created: $OUTPUT_ZIP"
SCRIPT
)
    
    # Run Docker container to package the function
    print_info "Running Docker container to package function..."
    
    if docker run --rm \
        -v "$python_dir:/source:ro" \
        -v "$output_dir:/output" \
        "$DOCKER_IMAGE_NAME" \
        -c "$package_script" \
        -- /source "/output/$zip_name" >/dev/null 2>&1; then
        
        if [[ -f "$zip_path" ]]; then
            local size
            size=$(du -h "$zip_path" | cut -f1)
            print_success "Package created successfully (Size: $size)"
            echo "$zip_path"  # Return the path
            zip_file=$zip_path
        else
            print_error "Package file was not created"
            return 1
        fi
    else
        print_error "Docker packaging failed"
        return 1
    fi
}

# Upload package to S3
upload_to_s3() {
    local zip_file="$1"
    local bucket="$2"
    
    local zip_name
    zip_name="$(basename "$zip_file")"
    
    print_info "Uploading to S3: s3 cp $zip_file s3://$bucket/$zip_name"
    
    # Check if AWS CLI is configured
    if ! aws sts get-caller-identity >/dev/null 2>&1; then
        print_error "AWS CLI is not configured. Please run 'aws configure' first."
        return 1
    fi
    
    # Upload file
    if aws s3 cp "$zip_file" "s3://$bucket/$zip_name" >/dev/null 2>&1; then
        print_success "Upload completed: s3://$bucket/$zip_name"
        
        # Get and display the S3 URL
        local region
        region=$(aws s3api get-bucket-location --bucket "$bucket" --query LocationConstraint --output text 2>/dev/null || echo "us-east-1")
        if [[ "$region" == "None" ]]; then
            region="us-east-1"
        fi
        
        print_info "S3 URL: https://${bucket}.s3.${region}.amazonaws.com/${zip_name}"
    else
        print_error "Failed to upload to S3"
        return 1
    fi
}

# Clean up Docker image
clean_docker_image() {
    print_info "Removing Docker image: $DOCKER_IMAGE_NAME"
    
    if docker rmi "$DOCKER_IMAGE_NAME" >/dev/null 2>&1; then
        print_success "Docker image removed"
    else
        print_error "Failed to remove Docker image (may be in use)"
    fi
}

# =============================================================================
# Main Script
# =============================================================================

main() {
    # Variables to store command line arguments
    local python_dir=""
    local output_dir="$DEFAULT_OUTPUT_DIR"
    local s3_bucket="$DEFAULT_S3_BUCKET"
    local skip_upload=false
    local clean_docker=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -p|--python-dir)
                python_dir="$2"
                shift 2
                ;;
            -o|--output-dir)
                output_dir="$2"
                shift 2
                ;;
            -s|--s3-bucket)
                s3_bucket="$2"
                shift 2
                ;;
            -n|--no-upload)
                skip_upload=true
                shift
                ;;
            -c|--clean)
                clean_docker=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                print_error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # Check if python directory is provided
    if [[ -z "$python_dir" ]]; then
        print_error "Python directory is required"
        show_help
        exit 1
    fi
    
    # Convert to absolute paths
    python_dir="$(cd "$python_dir" && pwd)"
    output_dir="$(cd "$output_dir" && pwd)"
    
    # Validate Python directory
    if ! validate_python_dir "$python_dir"; then
        exit 1
    fi
    
    # Create output directory if it doesn't exist
    if [[ ! -d "$output_dir" ]]; then
        print_info "Creating output directory: $output_dir"
        mkdir -p "$output_dir"
    fi
    
    print_info "Configuration:"
    print_info "  Python directory: $python_dir"
    print_info "  Output directory: $output_dir"
    print_info "  S3 bucket: $s3_bucket"
    print_info "  Skip upload: $skip_upload"
    print_info "  Clean Docker: $clean_docker"
    
    # Step 1: Build Docker image if needed
    build_docker_image
    
    # Step 2: Package Lambda function
    package_lambda "$python_dir" "$output_dir"

    if [[ -z "$zip_file" ]]; then
        print_error "Failed to create package"
        exit 1
    fi

    print_success "Package completed: zip file is $zip_file"
    # Step 3: Upload to S3 if requested
    if [[ "$skip_upload" == false ]]; then
        upload_to_s3 "$zip_file" "$s3_bucket"
    else
        print_info "Skipping S3 upload as requested"
    fi
    
    # Step 4: Clean up Docker image if requested
    if [[ "$clean_docker" == true ]]; then
        clean_docker_image
    fi
    
    print_success "Script completed successfully!"
    print_success "Package location: $zip_file"
}

# Run main function
main "$@"
