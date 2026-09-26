package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.util.Optional;

import com.vidyasahay.vidyasahay.enums.SchemeStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeEligibility;

@Repository
public interface ScholarshipSchemeRepository extends JpaRepository<ScholarshipScheme, UUID> {
	List<ScholarshipScheme> findByCreatedById(UUID userId);

	List<ScholarshipScheme> findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
			SchemeStatus status,
			LocalDate startDate,
			LocalDate endDate);

	@Query("select eligibility from ScholarshipSchemeEligibility eligibility where eligibility.scholarshipScheme.id = :schemeId")
	Optional<ScholarshipSchemeEligibility> findEligibilityBySchemeId(@Param("schemeId") UUID schemeId);

	@Query("select profession.id from ScholarshipSchemeProfession mapping join mapping.profession profession where mapping.scholarshipScheme.id = :schemeId")
	List<UUID> findEligibleProfessionIds(@Param("schemeId") UUID schemeId);

	@Query("select category.id from ScholarshipSchemeCategory mapping join mapping.category category where mapping.scholarshipScheme.id = :schemeId")
	List<UUID> findEligibleCategoryIds(@Param("schemeId") UUID schemeId);

	@Query("select mapping.documentType.id from ScholarshipSchemeRequiredDocument mapping where mapping.scholarshipScheme.id = :schemeId")
	List<UUID> findRequiredDocumentTypeIds(@Param("schemeId") UUID schemeId);
}
