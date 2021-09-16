resource "mongodbatlas_cluster" "oauth_cluster" {
  name                   = var.cluster_name
  project_id             = mongodbatlas_project.oauth2-project.id
  mongo_db_major_version = var.mongodb_version
  cluster_type           = "REPLICASET"
  replication_specs {
    num_shards = 1
    regions_config {
      region_name     = var.region
      electable_nodes = 3
      priority        = 7
      read_only_nodes = 0
    }
  }

  # provider_backup_enabled = false
  # auto_scaling_disk_gb_enabled = true
  provider_instance_size_name  = "M0"
  provider_name                = var.cloud_provider
}


output "connection_strings" {
  value = mongodbatlas_cluster.oauth_cluster.connection_strings.0.standard_srv
}
