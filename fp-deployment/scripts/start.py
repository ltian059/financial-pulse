#!/usr/bin/env python3


"""
Financial Pulse Account Service java process start script.

Usage: python3 start.py jar_name [app_dir]
"""

import os
import sys
import subprocess
import signal
from pathlib import Path
from logutil import log
from stop import ServiceStopper

# JVM options for the Java process
JAVA_OPTS = [
    "-Xmx512m", # Maximum heap size
    "-Xms256m", # Initial heap size
    "-Djava.security.egd=file:/dev/./urandom" # Use a faster entropy source for secure random numbers
    "-Djava.net.preferIPv6Addresses=true -Djava.net.preferIPv4Stack=false" # Prefer IPv6 over IPv4
]

class ServiceStarter():
    def __init__(self,  jar_name, app_dir="/opt/app"):
        self.app_dir = Path(app_dir) # Application directory
        self.env = self.app_dir / ".env" # Path to the .env file
        self.jar = self.app_dir / jar_name # Path to the Java JAR file
        self.pid = self.app_dir / "app.pid" # PID file to store the Java process ID
        self.log = self.app_dir / "app.log" # Log file for stdout and stderr


    def _load_environment(self):
        """
        Load .env file's environment variables into a dictionary and return it.
        :return: dict of environment variables
        """
        env_vars = {}
        if not self.env.exists():
            log.error(".env file not found!")
            sys.exit(1)
        try:
            with open(self.env, 'r') as f:
                # Read each line in the .env file and parse key-value pairs
                # key=value
                for line in f:
                    line = line.strip()
                    if line and not line.startswith("#"):
                        if '=' in line:
                            key, value = line.split("=", 1)
                            # os.environ is a dictionary representing the user's environmental variables
                            # After this, the java process started by this script will inherit these env vars
                            # To avoid polluting os.environ, we load into a separate dict first
                            env_vars[key.strip()] = value.strip()
            return env_vars
        except Exception as e:
            log.error(f"Fail to load .env file: {e}")
            sys.exit(1)


    def _check_jar_file(self):
        """Check if the java JAR file exists"""
        if not self.jar.exists():
            log.error("Java JAR file not found!")
            sys.exit(1)
        log.info(f"Found Java JAR file: {self.jar}")


    def _start_java_application(self, env_vars):
        """Start the Java application as a background process"""
        cmd = ["java"] + JAVA_OPTS + ["-jar", str(self.jar)]

        # Get a copy of the current environment and update with .env vars
        child_process_environ = os.environ.copy()
        child_process_environ.update(env_vars)

        try:
            #Open the log file
            with open(self.log, 'a') as log_f:
                process = subprocess.Popen(
                    cmd,
                    stdout=log_f,
                    stderr=log_f,
                    preexec_fn=os.setsid, # Start the process in a new session
                    env=child_process_environ
                )
            # Write the PID to the pid file
            with open(self.pid, 'w') as pid_f:
                pid_f.write(str(process.pid))

            log.info(f"Started Java application with PID {process.pid}")
            log.info(f"PID written to {self.pid}")

            if process.poll() is None:
                log.info("Java application is running.")
            else:
                log.error("Java application failed to start.")
                sys.exit(1)
        except Exception as e:
            log.error(f"Failed to start Java application: {e}")
            sys.exit(1)


    def _cleanup_handler(self, signum, frame):
        """Handle termination signals to clean up the Java process"""
        log.info(f"Received signal {signum}, terminating Java application...")
        try:
            stopper = ServiceStopper(self.app_dir)
            stopper.run()
        except Exception as e:
            log.error(f"Error during cleanup: {e}")
        sys.exit(1)

    def run(self):
        """Main execution function"""
        print("*" * 50)
        print("Financial Pulse Service Starter")
        print("*" * 50)

        signal.signal(signal.SIGINT, self._cleanup_handler)
        signal.signal(signal.SIGTERM, self._cleanup_handler)

        try:
            os.chdir(self.app_dir)
            env_vars = self._load_environment()
            self._check_jar_file()
            self._start_java_application(env_vars)
        except KeyboardInterrupt:
            log.warning("Startup interrupted by user")
            sys.exit(1)
        except Exception as e:
            log.error(f"Startup failed: {e}")
            sys.exit(1)

def main():
    """Entry point"""
    jar_name = sys.argv[1] if len(sys.argv) > 1 else None
    if not jar_name:
        print("Usage: python3 start.py jar_name [app_dir]")
        print("Example: python3 start.py account-service.jar /opt/app")
        sys.exit(1)
    app_dir = sys.argv[2] if len(sys.argv) > 2 else "/opt/app"

    manager = ServiceStarter(jar_name, app_dir)
    manager.run()

if __name__ == '__main__':
    main()