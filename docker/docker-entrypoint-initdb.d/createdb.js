db.auth('root', 'root@pass')

db = db.getSiblingDB('authserver')

db.createUser(
    {
        user: "idea",
        pwd: "ideapass",
        roles: [
            {
                role: "readWrite",
                db: "authserver"
            }
        ]
    }
)