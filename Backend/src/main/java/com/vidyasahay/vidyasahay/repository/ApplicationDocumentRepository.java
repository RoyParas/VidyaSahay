package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.ApplicationDocument;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationDocumentRepository extends JpaRepository<ApplicationDocument, UUID> {

	@EntityGraph(attributePaths = { "studentDocument", "studentDocument.student", "studentDocument.documentType",
			"studentDocument.verifiedBy" })
	List<ApplicationDocument> findAllByApplicationId(UUID applicationId);

	boolean existsByApplication_IdAndStudentDocument_Id(UUID applicationId, UUID studentDocumentId);
}
