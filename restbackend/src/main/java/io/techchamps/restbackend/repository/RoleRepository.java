package io.techchamps.restbackend.repository;

import io.techchamps.restbackend.entity.Role;
import io.techchamps.restbackend.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
