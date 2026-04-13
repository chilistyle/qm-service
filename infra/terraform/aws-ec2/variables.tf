variable "aws_region" {
  description = "AWS region for deployment"
  type        = string
  default     = "eu-central-1"
}

variable "instance_name" {
  description = "EC2 instance name"
  type        = string
  default     = "qm-service-ec2"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "m7i-flex.large"
}

variable "ssh_allowed_cidr" {
  description = "CIDR allowed to SSH into EC2"
  type        = string
  default     = "0.0.0.0/0"
}

variable "key_name" {
  description = "Existing AWS EC2 key pair name (optional)"
  type        = string
  default     = null
}

variable "root_volume_size" {
  description = "Root EBS volume size in GiB"
  type        = number
  default     = 40
}

variable "app_repo_url" {
  description = "Git repository URL with qm-service source"
  type        = string
  default     = "https://github.com/chilistyle/qm-service.git"
}

variable "app_repo_branch" {
  description = "Git branch to deploy"
  type        = string
  default     = "main"
}

variable "enable_https" {
  description = "Open HTTPS port 443 on security group"
  type        = bool
  default     = false
}

variable "extra_ingress_ports" {
  description = "Extra TCP ports to open publicly"
  type        = list(number)
  default     = []
}
