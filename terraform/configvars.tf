variable "mongodb_atlas_api_pub_key" {
  type        = string
  description = "The api public key"
}

variable "mongodb_atlas_api_pri_key" {
  type        = string
  description = "The API private key"
}

variable "mongodb_atlas_org_id" {
  type        = string
  description = "The MondoDB Atlas organisation ID"
}

variable "mongodb_atlas_database" {
  type        = string
  description = "The Databae name"
}

variable "mongodb_atlas_database_username" {
  type        = string
  description = "The Databae Username"
}

variable "mongodb_atlas_database_user_password" {
  type        = string
  description = "The Database Password"
}

variable "cloud_provider" {
  type        = string
  description = "Cloud provider must be AWS, GCP or AZURE"
}

variable "region" {
  type        = string
  description = "Cluster region"
}

variable "cluster_name" {
  type = string
}

variable "project_name" {
  type = string
}


variable "mongodb_version" {
  type = string
}

variable "ip_address" {
  type = string
}
