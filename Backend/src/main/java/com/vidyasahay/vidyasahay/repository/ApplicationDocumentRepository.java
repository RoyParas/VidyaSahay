package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.ApplicationDocument;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApplicationDocumentRepository extends JpaRepository<ApplicationDocument, UUID> {

	@EntityGraph(attributePaths = { "studentDocument", "studentDocument.student", "studentDocument.documentType",
			"studentDocument.verifiedBy" })
	List<ApplicationDocument> findAllByApplicationId(UUID applicationId);

	boolean existsByApplication_IdAndStudentDocument_Id(UUID applicationId, UUID studentDocumentId);

	@Query("select case when count(link) > 0 then true else false end from ApplicationDocument link " +
			"where link.studentDocument.id = :documentId and link.application.student.user.id = :userId")
	boolean existsByDocumentIdAndApplicationOwner(@Param("documentId") UUID documentId,
			@Param("userId") UUID userId);

	void deleteAllByApplicationId(UUID applicationId);
}
