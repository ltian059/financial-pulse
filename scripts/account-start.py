#!/usr/bin/env python3
"""
Financial Pulse Account Service Startup Script
"""

import os
import sys
import subprocess
import time
import signal
from pathlib import Path

class Colors:
    RED = '\033[31m'
    GREEN = '\033[32m'
    YELLOW = '\033[33m'
    BLUE = '\033[34m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

class AccountServiceManager:
    def __init__(self, app_dir="/opt/app"):
        self.app_dir = Path(app_dir)
        self.env_file = self.app_dir / ".env"
        self.jar_file = self.app_dir / "fp-account.jar"
        self.pid_file = self.app_dir / "app.pid"
        self.log_file = self.app_dir / "app.log"
        
    def log_info(self, message):
        print(f"{Colors.GREEN}[INFO]{Colors.RESET} {message}")
    
    def log_warning(self, message):
        print(f"{Colors.YELLOW}[WARNING]{Colors.RESET} {message}")
        
    def log_error(self, message):
        print(f"{Colors.RED}[ERROR]{Colors.RESET} {message}")
    
    def load_environment(self):
        """Load environment variables from .env file"""
        if not self.env_file.exists():
            self.log_error(".env file not found!")
            self.log_error("Please ensure the deployment process has created the .env file")
            sys.exit(1)
            
        self.log_info("Loading environment variables from .env file...")
        
        try:
            with open(self.env_file, 'r') as f:
                for line in f:
                    line = line.strip()
                    # Skip empty lines and comments
                    if line and not line.startswith('#'):
                        if '=' in line:
                            key, value = line.split('=', 1)
                            os.environ[key.strip()] = value.strip()
            
            self.log_info("Environment variables loaded successfully")
            
        except Exception as e:
            self.log_error(f"Failed to load environment variables: {e}")
            sys.exit(1)
    
    def validate_configuration(self):
        """Validate critical configuration"""
        self.log_info("Validating application configuration...")
        
        # Check JWT Secret
        jwt_secret = os.getenv('JWT_SECRET')
        if not jwt_secret:
            self.log_error("JWT_SECRET is not set")
            sys.exit(1)
            
        if len(jwt_secret) < 10:
            self.log_error("JWT_SECRET is too short (minimum 10 characters)")
            sys.exit(1)
        
        # Check SQS Configuration (if enabled)
        if os.getenv('AWS_SQS_ENABLED', '').lower() == 'true':
            sqs_email_queue = os.getenv('SQS_EMAIL_QUEUE_URL')
            if not sqs_email_queue:
                self.log_error("SQS_EMAIL_QUEUE_URL is not set but SQS is enabled")
                sys.exit(1)
            self.log_info("SQS configuration validated")
        
        self.log_info("Configuration validation completed")
    
    def check_jar_file(self):
        """Check if JAR file exists"""
        if not self.jar_file.exists():
            self.log_error("fp-account.jar not found!")
            self.log_error("Please deploy the application JAR file first")
            sys.exit(1)
            
        self.log_info(f"JAR file found: {self.jar_file}")
    
    def start_application(self):
        """Start the application"""
        self.log_info("Starting Financial Pulse Account Service...")
        
        # Log configuration info
        self.log_info(f"Environment: {os.getenv('SPRING_PROFILES_ACTIVE', 'default')}")
        self.log_info(f"AWS Region: {os.getenv('AWS_REGION', 'not set')}")
        self.log_info(f"Server Port: {os.getenv('SERVER_PORT', '8080')}")
        
        jwt_secret = os.getenv('JWT_SECRET', '')
        self.log_info(f"JWT Secret configured: {len(jwt_secret)} characters")
        
        # Set JVM options
        java_opts = [
            "-Xmx512m",
            "-Xms256m",
            "-Djava.security.egd=file:/dev/./urandom"
        ]
        
        # Prepare command
        cmd = ["java"] + java_opts + ["-jar", str(self.jar_file)]
        
        try:
            # Change to app directory
            os.chdir(self.app_dir)
            
            # Start process in background
            with open(self.log_file, 'a') as log_f:
                process = subprocess.Popen(
                    cmd,
                    stdout=log_f,
                    stderr=subprocess.STDOUT,
                    preexec_fn=os.setsid  # Create new session
                )
            
            # Save PID
            with open(self.pid_file, 'w') as pid_f:
                pid_f.write(str(process.pid))
            
            self.log_info(f"Application started with PID: {process.pid}")
            self.log_info(f"PID saved to: {self.pid_file}")
            
            # Brief wait to check if process started successfully
            time.sleep(2)
            
            if process.poll() is None:
                self.log_info("Application process is running")
            else:
                self.log_error("Application process exited immediately")
                sys.exit(1)
                
        except Exception as e:
            self.log_error(f"Failed to start application: {e}")
            sys.exit(1)
    
    def cleanup_handler(self, signum, frame):
        """Handle script interruption"""
        self.log_warning("Application startup interrupted")
        sys.exit(1)
    
    def run(self):
        """Main execution"""
        print("=" * 50)
        print("Financial Pulse Account Service Startup")
        print("=" * 50)
        
        # Set up signal handlers
        signal.signal(signal.SIGINT, self.cleanup_handler)
        signal.signal(signal.SIGTERM, self.cleanup_handler)
        
        try:
            self.load_environment()
            self.validate_configuration()
            self.check_jar_file()
            self.start_application()
            
            self.log_info("Startup completed successfully!")
            
        except KeyboardInterrupt:
            self.log_warning("Startup interrupted by user")
            sys.exit(1)
        except Exception as e:
            self.log_error(f"Unexpected error during startup: {e}")
            sys.exit(1)

def main():
    """Entry point"""
    app_dir = sys.argv[1] if len(sys.argv) > 1 else "/opt/app"
    
    manager = AccountServiceManager(app_dir)
    manager.run()

if __name__ == "__main__":
    main()