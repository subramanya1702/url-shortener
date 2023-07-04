terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region = "region"
}

# Retrieve existing VPC and subnet details
data "aws_vpc" "existing_vpc" {
  id = "vpc-id"  # Update with your existing VPC ID
}

data "aws_subnet" "existing_subnet" {
  vpc_id = data.aws_vpc.existing_vpc.id
  id     = "subnet-id"  # Update with your existing subnet ID
}

# Create a new security group
resource "aws_security_group" "example_sg" {
  name        = "example-security-group"
  description = "Example security group"
  vpc_id      = data.aws_vpc.existing_vpc.id

  # Define inbound rules
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Send Anywhere"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Launch an EC2 instance
resource "aws_instance" "app_server" {
  ami           = "ami-id"
  instance_type = "t2.xx"
  key_name      = "key_name"

  subnet_id              = data.aws_subnet.existing_subnet.id
  vpc_security_group_ids = [aws_security_group.example_sg.id]

  tags = {
    Name = "Name"
  }
}
