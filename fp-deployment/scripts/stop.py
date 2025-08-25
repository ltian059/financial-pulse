#!/usr/bin/env python3

"""
Financial Pulse Account Service Stop Script

Usage: python3 stop.py jar_name [app_dir]
"""

import os
import sys
import time
import signal
import subprocess
from pathlib import Path
from logutil import log

class ServiceStopper:
    def __init__(self, jar_name, app_dir="/opt/app"):
        self.app_dir = Path(app_dir)
        self.pid = self.app_dir / "app.pid"
        self.jar = jar_name

    def _is_process_running(self, pid):
        try:
            os.kill(pid, 0) # Signal 0 just checks if process exists
            return True
        except (OSError, ProcessLookupError):
            return False


    def _terminate_process(self, pid, timeout=30):
        """Gracefully terminate a process"""
        try:
            os.kill(pid, signal.SIGTERM)
            # Wait for a graceful shutdown
            for i in range(timeout):
                if not self._is_process_running(pid):
                    log.info(f"Process {pid} terminated gracefully")
                    return True
                if i % 5 == 0:
                    log.info(f"Waiting for graceful shutdown... ({i}/{timeout})")
                time.sleep(1)
            # Force kill if still running
            if self._is_process_running(pid):
                log.warning("Process did not terminate gracefully, sending SIGKILL")
                os.kill(pid, signal.SIGKILL)
                time.sleep(2)
                if not self._is_process_running(pid):
                    log.info(f"Process {pid} force killed successfully")
                    return True
                else:
                    log.error(f"Failed to kill process {pid}")
                    return False
        except ProcessLookupError:
            log.info(f"Process {pid} does not exist")
            return True
        except PermissionError:
            log.error(f"No permission to terminate process {pid}")
            return False
        except Exception as e:
            log.error(f"Error terminating process {pid}: {e}")
            return False

    def _stop_by_pid(self):
        """Stop the application using the PID file"""
        if not self.pid.exists():
            log.warning("PID file does not exist")
            return False
        try:
            with open(self.pid, 'r') as f:
                pid = int(f.read().strip())
            if not self._is_process_running(pid):
                self.pid.unlink() # Remove stale PID file
                return True
            is_success = self._terminate_process(pid)
            if is_success:
                self.pid.unlink()
            return is_success
        except ValueError:
            log.error("Invalid PID in PID file")
            return False
        except Exception:
            log.error("Failed to read PID file")
            return False

    def _stop_by_process_search(self):
        """Stop the application by searching for the Java process"""
        try:
            cmd = ["pgrep", "-f", self.jar]
            res = subprocess.run(
                cmd,
                capture_output=True
            )
            pids = []
            if res.returncode == 0:
                pids = []
                for pid in res.stdout.strip().split('\n'):
                    if pid.strip():
                        pids.append(int(pid.strip()))
            if not pids:
                return True

            all_success = True
            for pid in pids:
                if self._is_process_running(pid):
                    if not self._terminate_process(pid):
                        all_success = False
            if not all_success:
                log.warning("Stop by process search: Some processes failed to terminate")
            else:
                log.info("All processes terminated successfully by process search")
            return all_success
        except Exception as e:
            log.warning(f"Failed to find Java processes: {e}")
            return False

    def run(self):
        """Main execution function"""
        print("*" * 50)
        print("Financial Pulse Account Service Stopper")
        print("*" * 50)

        os.chdir(self.app_dir)
        stopped = self._stop_by_pid()
        if not stopped:
            log.warning("Stop by PID file failed or no PID file found, trying process search...")
            stopped = self._stop_by_process_search()

        if stopped:
            log.info("Account Service stopped successfully")
            sys.exit(0)
        else:
            log.error("Failed to stop Account Service")
            sys.exit(1)


def main():
    jar_name = sys.argv[1] if len(sys.argv) > 1 else None
    if not jar_name:
        print("Usage: python3 stop.py jar_name [app_dir]")
        print("Example: python3 stop.py fp-account.jar /opt/app")
        sys.exit(1)
    app_dir = sys.argv[2] if len(sys.argv) > 2 else "/opt/app"
    stopper = ServiceStopper(jar_name, app_dir)
    stopper.run()


if __name__ == '__main__':
    main()