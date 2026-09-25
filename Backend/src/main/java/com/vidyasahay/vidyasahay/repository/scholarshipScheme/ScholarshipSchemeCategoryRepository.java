package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeCategory;

@Repository
public interface ScholarshipSchemeCategoryRepository extends JpaRepository<ScholarshipSchemeCategory, UUID> {

    List<ScholarshipSchemeCategory> findByScholarshipSchemeId(UUID scholarshipSchemeId);
    
    Set<ScholarshipSchemeCategory> findByCategoryId(UUID categoryId);
    
    void deleteByScholarshipSchemeId(UUID scholarshipSchemeId);

}
