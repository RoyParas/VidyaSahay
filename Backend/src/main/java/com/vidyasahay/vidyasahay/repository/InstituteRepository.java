package com.vidyasahay.vidyasahay.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vidyasahay.vidyasahay.entity.Institute;

public interface InstituteRepository extends JpaRepository<Institute,UUID> {

    @Override
    @EntityGraph(attributePaths = {"user","user.role","address" })
   List<Institute> findAll();

    @Override
    @EntityGraph(attributePaths = {"user","user.role","address" })
   Optional<Institute> findById(UUID id);

    @EntityGraph(attributePaths = {"user","user.role","address" })
   Optional<Institute> findByUserId(UUID userId);

   boolean existsByNameIgnoreCase(String name);
}