terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "1.0.1"
    }
    aws = {
      source  = "hashicorp/aws"
      version = "3.57.0"
    }
  }
  required_version = ">=0.13"
}
