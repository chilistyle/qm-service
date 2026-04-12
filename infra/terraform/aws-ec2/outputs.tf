output "instance_id" {
  description = "Created EC2 instance ID"
  value       = aws_instance.qm_service.id
}

output "public_ip" {
  description = "Public IP of EC2 instance"
  value       = aws_instance.qm_service.public_ip
}

output "public_dns" {
  description = "Public DNS name of EC2 instance"
  value       = aws_instance.qm_service.public_dns
}

output "app_url" {
  description = "Base HTTP URL of qm-service via nginx"
  value       = "http://${aws_instance.qm_service.public_ip}"
}

output "ssh_command" {
  description = "SSH command template"
  value       = var.key_name != null ? "ssh -i <path-to-private-key> ubuntu@${aws_instance.qm_service.public_ip}" : "Key pair not set: add key_name to connect via SSH"
}
