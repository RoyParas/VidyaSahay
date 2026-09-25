package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;

@Repository
public interface ScholarshipSchemeRepository extends JpaRepository<ScholarshipScheme, UUID> {
	List<ScholarshipScheme> findByCreatedById(UUID userId);
}