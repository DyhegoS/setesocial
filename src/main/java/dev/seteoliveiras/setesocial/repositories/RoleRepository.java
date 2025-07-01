package dev.seteoliveiras.setesocial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.seteoliveiras.setesocial.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

    Role findByName(String name);

}
