#!/usr/bin/env python3
"""
Environment File Validation Script

This script validates that all environment variables in the .env file are properly set
and not empty. It should be run AFTER the .env file is created.

Usage: python scripts/validate-env-file.py [env-file-path]
Examples:
  python scripts/validate-env-file.py .env
  python scripts/validate-env-file.py (defaults to .env)
"""

import os
import sys
from pathlib import Path

class Colors:
    RED = '\033[31m'
    GREEN = '\033[32m'
    YELLOW = '\033[33m'
    BLUE = '\033[34m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

class EnvFileValidator:
    def __init__(self, env_file_path=".env"):
        self.env_file_path = Path(env_file_path)
        self.errors = []
        self.warnings = []
        
    def log_info(self, message):
        print(f"{Colors.GREEN}[INFO]{Colors.RESET} {message}")
    
    def log_warning(self, message):
        print(f"{Colors.YELLOW}[WARNING]{Colors.RESET} {message}")
        
    def log_error(self, message):
        print(f"{Colors.RED}[ERROR]{Colors.RESET} {message}")

    def validate_env_file(self):
        """Validate that .env file exists and all variables are set"""
        print(f"{Colors.BOLD}{Colors.BLUE}Environment File Validation{Colors.RESET}")
        print("=" * 50)
        
        if not self.env_file_path.exists():
            self.log_error(f"Environment file not found: {self.env_file_path}")
            return False
        
        self.log_info(f"Validating environment file: {self.env_file_path}")
        
        # Read and parse .env file
        env_vars = {}
        line_num = 0
        
        try:
            with open(self.env_file_path, 'r') as f:
                for line in f:
                    line_num += 1
                    line = line.strip()
                    
                    # Skip empty lines and comments
                    if not line or line.startswith('#'):
                        continue
                    
                    # Parse key=value
                    if '=' in line:
                        key, value = line.split('=', 1)
                        key = key.strip()
                        value = value.strip()
                        env_vars[key] = value
                    else:
                        self.warnings.append(f"Line {line_num}: Invalid format (no '=' found): {line}")
        
        except Exception as e:
            self.log_error(f"Failed to read environment file: {e}")
            return False
        
        if not env_vars:
            self.log_error("No environment variables found in file")
            return False
            
        self.log_info(f"Found {len(env_vars)} environment variables")
        
        # Validate each variable
        empty_vars = []
        valid_vars = []
        
        for key, value in env_vars.items():
            if not value:  # Empty or None
                empty_vars.append(key)
                self.errors.append(f"Environment variable '{key}' is empty")
            else:
                valid_vars.append(key)
        
        # Report results
        if valid_vars:
            self.log_info(f"Valid variables ({len(valid_vars)}):")
            for var in sorted(valid_vars):
                value = env_vars[var]
                # Mask sensitive values
                if any(sensitive in var.upper() for sensitive in ['SECRET', 'PASSWORD', 'KEY']):
                    display_value = f"{'*' * min(8, len(value))} ({len(value)} chars)"
                else:
                    display_value = value[:50] + "..." if len(value) > 50 else value
                print(f"   {Colors.GREEN}[+]{Colors.RESET} {var} = {display_value}")
        
        if empty_vars:
            self.log_error(f"Empty variables ({len(empty_vars)}):")
            for var in sorted(empty_vars):
                print(f"   {Colors.RED}[-]{Colors.RESET} {var}")
        
        if self.warnings:
            self.log_warning(f"Warnings ({len(self.warnings)}):")
            for warning in self.warnings:
                print(f"   {Colors.YELLOW}[!]{Colors.RESET} {warning}")
        
        # Summary
        print(f"\n{Colors.BOLD}=== Validation Summary ==={Colors.RESET}")
        
        if empty_vars:
            self.log_error(f"Validation failed! {len(empty_vars)} empty variables found.")
            self.log_error("Please check your GitHub Actions variables and secrets configuration.")
            return False
        else:
            self.log_info("All environment variables are properly set!")
            return True

def main():
    """Main execution function"""
    env_file = sys.argv[1] if len(sys.argv) > 1 else ".env"
    
    validator = EnvFileValidator(env_file)
    is_valid = validator.validate_env_file()
    
    sys.exit(0 if is_valid else 1)

if __name__ == "__main__":
    main()