# Terraform: AWS EC2 deployment for qm-service

This Terraform stack creates:
- Security Group with HTTP and SSH access (plus optional extra ports)
- Single Ubuntu EC2 instance
- Bootstrap script (user-data) that installs Docker, clones repository, and starts docker compose

## Prerequisites

- Terraform 1.6+
- AWS account and IAM credentials configured (AWS CLI profile or env vars)
- Existing EC2 key pair in target region (optional but recommended)

## Quick start

1. Copy variables file:

```bash
cp terraform.tfvars.example terraform.tfvars
```

2. Edit terraform.tfvars:
- set key_name to your EC2 key pair
- limit ssh_allowed_cidr to your public IP
- set app_repo_url to your Git repository

3. Initialize and deploy:

```bash
terraform init
terraform plan
terraform apply
```

4. Check outputs:

```bash
terraform output
```

After bootstrap, application should be available via output app_url (port 80).

## Notes

- user_data runs docker compose directly from repository root.
- For production hardening, put database services behind private networking and add TLS termination.
- You can open additional ports using extra_ingress_ports if you need direct access to internal services.
