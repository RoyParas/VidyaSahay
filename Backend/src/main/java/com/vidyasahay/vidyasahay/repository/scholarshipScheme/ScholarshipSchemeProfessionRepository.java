package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeProfession;

@Repository
public interface ScholarshipSchemeProfessionRepository extends JpaRepository<ScholarshipSchemeProfession, UUID> {
	
	List<ScholarshipSchemeProfession> findByScholarshipSchemeId(UUID scholarshipSchemeId);
	
	List<ScholarshipSchemeProfession> findByProfessionId(UUID professionId);
	
	void deleteByScholarshipSchemeId(UUID scholarshipSchemeId);

}

