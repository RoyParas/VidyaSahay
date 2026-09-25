package com.vidyasahay.vidyasahay.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.enums.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

	Optional<Role> findByName(RoleName student);

}
