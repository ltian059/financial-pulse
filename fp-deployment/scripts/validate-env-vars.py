#!/usr/bin/env python3
"""
Environment Variables Validation Script (Simple Version)
"""

import os
import sys
import re
from scripts.logutil import log

# Service configuration mapping
service_configs = {
    'account': {
        'config_files': ['fp-account/src/main/resources/application-dev.yml'],
        'workflow_file': '.github/workflows/deploy-account.yml',
        'service_name': 'Account Service'
    },
    'follow': {
        'config_files': ['fp-follow/src/main/resources/application-dev.yml'],
        'workflow_file': '.github/workflows/deploy-follow.yml',
        'service_name': 'Follow Service'
    }
}

def extract_placeholders(content):
    """Extract placeholder variables from content using pattern ${VARIABLE_NAME}"""
    placeholder_pattern = r'\$\{([A-Z_][A-Z0-9_]*)\}'
    matches = re.findall(placeholder_pattern, content)
    return sorted(list(set(matches)))

def extract_workflow_variables(workflow_content):
    """Extract environment variables from GitHub Actions workflow"""
    secrets_pattern = r'\$\{\{\s*secrets\.([A-Z_][A-Z0-9_]*)\s*\}\}'
    vars_pattern = r'\$\{\{\s*vars\.([A-Z_][A-Z0-9_]*)\s*\}\}'
    
    variables = set()
    
    # Extract secrets
    secrets_matches = re.findall(secrets_pattern, workflow_content)
    variables.update(secrets_matches)
    
    # Extract variables
    vars_matches = re.findall(vars_pattern, workflow_content)
    variables.update(vars_matches)
    
    return sorted(list(variables))

def validate_service(service_name):
    """Validate a single service configuration"""
    config = service_configs.get(service_name)
    if not config:
        log.error(f"Unknown service: {service_name}")
        return False

    print(f"\n=== Validating {config['service_name']} ===")

    # Check if workflow file exists
    if not os.path.exists(config['workflow_file']):
        log.error(f"Workflow file not found: {config['workflow_file']}")
        return False

    # Collect all placeholders from configuration files
    all_placeholders = set()
    
    for config_file in config['config_files']:
        if not os.path.exists(config_file):
            log.warning(f"Configuration file not found: {config_file}")
            continue

        try:
            with open(config_file, 'r', encoding='utf-8') as file:
                config_content = file.read()
                placeholders = extract_placeholders(config_content)
                
                log.info(f"Found {len(placeholders)} placeholders in {config_file}")
                if placeholders:
                    print(f"   Variables: {', '.join(placeholders)}")
                
                all_placeholders.update(placeholders)
                
        except Exception as e:
            log.error(f"Error processing {config_file}: {e}")
            return False

    if not all_placeholders:
        log.info("No placeholders found - nothing to validate")
        return True

    # Extract variables from workflow file
    try:
        with open(config['workflow_file'], 'r', encoding='utf-8') as file:
            workflow_content = file.read()
            workflow_variables = extract_workflow_variables(workflow_content)
            
            log.info(f"Found {len(workflow_variables)} variables in workflow")
            if workflow_variables:
                print(f"   Variables: {', '.join(workflow_variables)}")
                
    except Exception as e:
        log.error(f"Error processing workflow file {config['workflow_file']}: {e}")
        return False

    # Compare placeholders with workflow variables
    workflow_vars_set = set(workflow_variables)
    missing_variables = []
    defined_variables = []

    for placeholder in all_placeholders:
        if placeholder in workflow_vars_set:
            defined_variables.append(placeholder)
        else:
            missing_variables.append(placeholder)

    # Report results
    if defined_variables:
        log.info(f"Properly defined variables ({len(defined_variables)}):")
        for variable in sorted(defined_variables):
            print(f"   [+] {variable}")

    if missing_variables:
        log.error(f"Missing variables in workflow ({len(missing_variables)}):")
        for variable in sorted(missing_variables):
            print(f"   [-] {variable}")
        return False

    # Check for unused workflow variables
    unused_variables = [var for var in workflow_variables if var not in all_placeholders]
    if unused_variables:
        log.warning(f"Variables defined in workflow but not used ({len(unused_variables)}):")
        for variable in sorted(unused_variables):
            print(f"   [?] {variable}")

    return True

def main():
    """Main execution function"""
    args = sys.argv[1:]
    target_service = args[0] if args else None

    print("Environment Variables Validation")
    print("======================================")
    
    if target_service and target_service not in service_configs:
        log.error(f"Unknown service '{target_service}'")
        log.error(f"Available services: {', '.join(service_configs.keys())}")
        sys.exit(1)

    services_to_validate = [target_service] if target_service else list(service_configs.keys())
    all_valid = True

    for service_name in services_to_validate:
        is_valid = validate_service(service_name)
        if not is_valid:
            all_valid = False

    print(f"\n=== Validation Summary ===")
    if all_valid:
        print("SUCCESS: All environment variables are properly configured!")
        sys.exit(0)
    else:
        print("FAILED: Environment variable validation failed!")
        print("Please fix the errors above before deploying.")
        sys.exit(1)

if __name__ == "__main__":
    main()