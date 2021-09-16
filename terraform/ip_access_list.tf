resource "mongodbatlas_project_ip_access_list" "ip" {
  project_id = mongodbatlas_project.oauth2-project.id
  ip_address = var.ip_address
  comment    = "Ip address to access the cluster"
}

output "ipaccesslist" {
  value = mongodbatlas_project_ip_access_list.ip.ip_address
}
