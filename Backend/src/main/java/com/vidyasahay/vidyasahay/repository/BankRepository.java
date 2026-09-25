package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.Bank;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {

    boolean existsByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = "user")
    List<Bank> findAllBy();

    @EntityGraph(attributePaths = "user")
    Optional<Bank> findOneById(UUID bankId);

    Optional<Bank> findByUserId(UUID userId);
}
