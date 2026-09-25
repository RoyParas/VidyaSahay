package com.vidyasahay.vidyasahay.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.Profession;

@Repository
public interface ProfessionRepository
        extends JpaRepository<Profession, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            UUID professionId
    );
}