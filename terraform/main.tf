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
  id = "vpc-id"
}

data "aws_subnet" "existing_subnet" {
  vpc_id = data.aws_vpc.existing_vpc.id
  id     = "subnet-id"
}

# Create a new security group for data server
resource "aws_security_group" "data_server_sg" {
  name        = "data-server-security-group"
  description = "Data Server security group"
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

  ingress {
    from_port   = 6379
    to_port     = 6379
    protocol    = "tcp"
    security_groups = [aws_security_group.app_server_sg.id]
  }

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    security_groups = [aws_security_group.app_server_sg.id]
  }

  # Define outbound rules
  egress {
    description = "Send Anywhere"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Create a new security group for app server
resource "aws_security_group" "app_server_sg" {
  name        = "app-server-security-group"
  description = "App Server security group"
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

  ingress {
    from_port   = 8080
    to_port     = 8080
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


# Launch an EC2 instance for Data Server
resource "aws_instance" "data_server" {
  ami           = "ami-id"
  instance_type = "t2.xx"
  key_name      = "key_name"

  subnet_id              = data.aws_subnet.existing_subnet.id
  vpc_security_group_ids = [aws_security_group.data_server_sg.id]

  tags = {
    Name = "Data Server"
  }
}

# Launch an EC2 instance for App Server
resource "aws_instance" "app_server" {
  ami           = "ami-id"
  instance_type = "t2.xx"
  key_name      = "key_name"

  subnet_id              = data.aws_subnet.existing_subnet.id
  vpc_security_group_ids = [aws_security_group.app_server_sg.id]

  tags = {
    Name = "App Server"
  }
}