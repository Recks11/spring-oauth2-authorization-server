package dev.rexijie.auth.repository;

import dev.rexijie.auth.model.authority.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByName(String name);
}
