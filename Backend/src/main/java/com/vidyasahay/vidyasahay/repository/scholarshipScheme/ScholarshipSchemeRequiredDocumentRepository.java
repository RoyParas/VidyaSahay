package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeRequiredDocument;

@Repository
public interface ScholarshipSchemeRequiredDocumentRepository extends JpaRepository<ScholarshipSchemeRequiredDocument, UUID> {

	List<ScholarshipSchemeRequiredDocument> findByScholarshipSchemeId(UUID scholarshipSchemeId);
	
	void deleteByScholarshipSchemeId(UUID scholarshipSchemeId);
	
    List<ScholarshipSchemeRequiredDocument>
    findByScholarshipSchemeIdOrderByDocumentTypeNameAsc(
            UUID scholarshipSchemeId
    );
}