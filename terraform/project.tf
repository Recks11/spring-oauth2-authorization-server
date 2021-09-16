resource "mongodbatlas_project" "oauth2-project" {
  name   = var.project_name
  org_id = var.mongodb_atlas_org_id
}


output "project_name" {
  value = mongodbatlas_project.oauth2-project.name
}