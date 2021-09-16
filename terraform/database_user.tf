// configure db users https://docs.atlas.mongodb.com/security-add-mongodb-users/

resource "mongodbatlas_database_user" "user" {
  username = var.mongodb_atlas_database_username
  password = var.mongodb_atlas_database_user_password
  project_id = mongodbatlas_project.oauth2-project.id
  auth_database_name = "admin"


  roles {
    role_name = "readWrite"
    database_name = var.mongodb_atlas_database
  }

  labels {
       key = "Name"
       value = "Main DB user"
    }
}

  output "database_user" {
     value = mongodbatlas_database_user.user.username
  }
