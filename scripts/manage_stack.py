import boto3
import sys
import time
from botocore.exceptions import ClientError, WaiterError


"""
Primarily used for CI/CD to create and delete SSM VPC endpoints stack.

This is to manage my development budget as the endpoints will incur costs.

"""
cf = boto3.client("cloudformation", region_name="us-west-1")

def wait_for_stack(stack_name, action):
    """Wait until stack reaches a terminal state."""
    waiter_name = "stack_{}_complete".format("create" if action == "create" else "delete")
    waiter = cf.get_waiter(waiter_name)
    try:
        print(f" Waiting for {stack_name} to {action}...")
        waiter.wait(StackName=stack_name)
        print(f" Stack {stack_name} {action}d successfully")
    except WaiterError as e:
        stack_status = get_stack_status(stack_name)
        print(f"ERROR: Stack {stack_name} failed to {action}: {e}")
        sys.exit(1)

def get_stack_status(stack_name):
    try:
        response = cf.describe_stacks(StackName=stack_name)
        return response["Stacks"][0]["StackStatus"]
    except ClientError as e:
        if "does not exist" in str(e):
            return "NOT_FOUND"
        raise

def create_stack(stack_name, template_file, params):
    """
    Creates a CloudFormation stack.
    This function is idempotent:
    - If the stack exists and is stable, it does nothing.
    - If the stack exists in a failed state, it deletes and recreates it.
    - If the stack doesn't exist, it creates it.
    """
    status = get_stack_status(stack_name)
    print(f"Current status of stack '{stack_name}': {status}")
    # If stack is already created and stable, no action is needed.
    if status in ["CREATE_COMPLETE", "UPDATE_COMPLETE"]:
        print(f"✅ Stack '{stack_name}' already exists and is in a stable state. Skipping creation.")
        return
    # If stack exists but is in a failed/transient state, delete it first.
    if status not in ["NOT_FOUND", "DELETE_COMPLETE"]:
        print(f"⚠️ Stack '{stack_name}' exists but is in a failed state ({status}).")
        print("Attempting to delete the stack before recreating...")
        delete_stack(stack_name)
    print(f"🚀 Creating stack: {stack_name}")
    with open(template_file, "r") as f:
        template_body = f.read()
    try:
        cf.create_stack(
            StackName=stack_name,
            TemplateBody=template_body,
            Parameters=params,
            Capabilities=["CAPABILITY_NAMED_IAM"],
        )
        wait_for_stack(stack_name, "create")
    except WaiterError as e:
        # If waiting fails, the stack is in a bad state. Attempt cleanup.
        print(f"Stack creation failed. Attempting cleanup of failed stack '{stack_name}'...")
        delete_stack(stack_name)
        sys.exit(1)

def delete_stack(stack_name):
    """Deletes a CloudFormation stack and waits for completion."""
    # Check if stack exists before trying to delete
    if get_stack_status(stack_name) == "NOT_FOUND":
        print(f"Stack '{stack_name}' does not exist. Nothing to delete.")
        return
    print(f" Deleting stack: {stack_name}")
    cf.delete_stack(StackName=stack_name)
    try:
        wait_for_stack(stack_name, "delete")
    except WaiterError:
        status = get_stack_status(stack_name)
        print(f" Delete failed, current status: {status}")
        sys.exit(1)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python manage_stack.py <create|delete> <stack-name> [template-file]")
        sys.exit(1)

    action, stack_name = sys.argv[1], sys.argv[2]
    if action == "create":
        template_file = sys.argv[3]
        parameters = [
            {"ParameterKey": "Environment", "ParameterValue": "dev"},
            {"ParameterKey": "VPCStackName", "ParameterValue": "fp-vpc-dev"},
        ]
        create_stack(stack_name, template_file, parameters)
    elif action == "delete":
        delete_stack(stack_name)
    else:
        print("Invalid action: must be create or delete")
        sys.exit(1)
