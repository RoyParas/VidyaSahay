package com.vidyasahay.vidyasahay.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.DocumentType;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, UUID> {
	List<DocumentType> findAllById(UUID documentIds);
}