package com.vidyasahay.vidyasahay.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vidyasahay.vidyasahay.entity.StudentDocument;

public interface StudentDocumentRepository extends JpaRepository<StudentDocument, UUID> {

	@Override
	@EntityGraph(attributePaths = { "student", "student.user", "documentType", "verifiedBy" })
	Optional<StudentDocument> findById(UUID id);

	@EntityGraph(attributePaths = { "student", "student.user", "documentType", "verifiedBy" })

	List<StudentDocument> findByStudentId(UUID studentId);

	@EntityGraph(attributePaths = { "student", "documentType", "verifiedBy" })
	List<StudentDocument> findAllByIdInAndStudentId(Collection<UUID> ids, UUID studentId);

	Optional<StudentDocument>
	findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
	        UUID studentId,
	        UUID documentTypeId
	);
}
