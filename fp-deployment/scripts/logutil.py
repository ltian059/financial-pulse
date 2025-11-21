class Colors:
    RED = '\033[31m'
    GREEN = '\033[32m'
    YELLOW = '\033[33m'
    BLUE = '\033[34m'
    RESET = '\033[0m'
    BOLD = '\033[1m'


class _Logger:
    def info(self, message):
        print(f"{Colors.GREEN}[INFO]{Colors.RESET} {message}")

    def error(self, message):
        print(f"{Colors.RED}[ERROR]{Colors.RESET} {message}")


    def warning(self, message):
        print(f"{Colors.YELLOW}[WARNING]{Colors.RESET} {message}")

    def bold(self, message):
        print(f"{Colors.BOLD}{message}{Colors.RESET}")


log = _Logger()
