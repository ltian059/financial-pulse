#!/usr/bin/env python3
"""
Financial Pulse Account Service Stop Script
"""

import os
import sys
import time
import signal
import subprocess
from pathlib import Path

class Colors:
    RED = '\033[31m'
    GREEN = '\033[32m'
    YELLOW = '\033[33m'
    BLUE = '\033[34m'
    RESET = '\033[0m'
    BOLD = '\033[1m'

class AccountServiceStopper:
    def __init__(self, app_dir="/opt/app"):
        self.app_dir = Path(app_dir)
        self.pid_file = self.app_dir / "app.pid"
        self.jar_name = "fp-account.jar"
        
    def log_info(self, message):
        print(f"{Colors.GREEN}[INFO]{Colors.RESET} {message}")
    
    def log_warning(self, message):
        print(f"{Colors.YELLOW}[WARNING]{Colors.RESET} {message}")
        
    def log_error(self, message):
        print(f"{Colors.RED}[ERROR]{Colors.RESET} {message}")
    
    def is_process_running(self, pid):
        """Check if process is running"""
        try:
            os.kill(pid, 0)  # Signal 0 just checks if process exists
            return True
        except (OSError, ProcessLookupError):
            return False
    
    def find_java_processes(self):
        """Find running Java processes with our JAR"""
        try:
            result = subprocess.run(
                ["pgrep", "-f", self.jar_name],
                capture_output=True,
                text=True
            )
            
            if result.returncode == 0:
                pids = [int(pid.strip()) for pid in result.stdout.strip().split('\n') if pid.strip()]
                return pids
            else:
                return []
                
        except Exception as e:
            self.log_warning(f"Failed to find Java processes: {e}")
            return []
    
    def terminate_process(self, pid, timeout=30):
        """Gracefully terminate a process"""
        try:
            self.log_info(f"Sending SIGTERM to process {pid}")
            os.kill(pid, signal.SIGTERM)
            
            # Wait for graceful shutdown
            for i in range(timeout):
                if not self.is_process_running(pid):
                    self.log_info("Process terminated gracefully")
                    return True
                    
                if i % 5 == 0:  # Log every 5 seconds
                    print(f"Waiting for graceful shutdown... ({i}/{timeout})")
                time.sleep(1)
            
            # Force kill if still running
            if self.is_process_running(pid):
                self.log_warning("Process still running, forcing termination...")
                os.kill(pid, signal.SIGKILL)
                time.sleep(2)
                
                if not self.is_process_running(pid):
                    self.log_info("Process forcefully terminated")
                    return True
                else:
                    self.log_error("Failed to terminate process")
                    return False
            
            return True
            
        except ProcessLookupError:
            self.log_info("Process already terminated")
            return True
        except PermissionError:
            self.log_error(f"Permission denied to terminate process {pid}")
            return False
        except Exception as e:
            self.log_error(f"Error terminating process {pid}: {e}")
            return False
    
    def stop_by_pid_file(self):
        """Stop service using PID file"""
        if not self.pid_file.exists():
            return False
            
        try:
            with open(self.pid_file, 'r') as f:
                pid = int(f.read().strip())
            
            self.log_info(f"Found PID file with PID: {pid}")
            
            if not self.is_process_running(pid):
                self.log_info("Process is not running")
                self.pid_file.unlink()  # Remove stale PID file
                return True
            
            success = self.terminate_process(pid)
            
            if success:
                self.pid_file.unlink()  # Remove PID file
                self.log_info("Account Service stopped successfully")
            
            return success
            
        except ValueError:
            self.log_error("Invalid PID in PID file")
            self.pid_file.unlink()  # Remove invalid PID file
            return False
        except Exception as e:
            self.log_error(f"Error reading PID file: {e}")
            return False
    
    def stop_by_process_search(self):
        """Stop service by searching for Java processes"""
        pids = self.find_java_processes()
        
        if not pids:
            self.log_info("No running Account Service processes found")
            return True
        
        self.log_info(f"Found {len(pids)} running {self.jar_name} process(es): {pids}")
        
        success = True
        for pid in pids:
            if not self.terminate_process(pid):
                success = False
        
        if success:
            self.log_info("All Account Service processes terminated")
        else:
            self.log_warning("Some processes may not have been terminated successfully")
        
        return success
    
    def run(self):
        """Main execution"""
        print("=" * 50)
        print("Financial Pulse Account Service Stop")
        print("=" * 50)
        
        # Change to app directory
        try:
            os.chdir(self.app_dir)
        except Exception as e:
            self.log_error(f"Failed to change to app directory {self.app_dir}: {e}")
            sys.exit(1)
        
        success = True
        
        # First try to stop using PID file
        if self.pid_file.exists():
            self.log_info("Attempting to stop service using PID file...")
            success = self.stop_by_pid_file()
        else:
            self.log_warning("No PID file found, searching for running processes...")
            success = self.stop_by_process_search()
        
        # Double-check by searching for any remaining processes
        remaining_pids = self.find_java_processes()
        if remaining_pids:
            self.log_warning(f"Found remaining processes: {remaining_pids}")
            self.log_info("Attempting to clean up remaining processes...")
            for pid in remaining_pids:
                self.terminate_process(pid)
            success = False
        
        if success:
            self.log_info("Stop script completed successfully")
            sys.exit(0)
        else:
            self.log_error("Stop script completed with errors")
            sys.exit(1)

def main():
    """Entry point"""
    app_dir = sys.argv[1] if len(sys.argv) > 1 else "/opt/app"
    
    stopper = AccountServiceStopper(app_dir)
    stopper.run()

if __name__ == "__main__":
    main()