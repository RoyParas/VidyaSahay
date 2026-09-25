package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeEligibility;

@Repository
public interface ScholarshipSchemeEligibilityRepository extends JpaRepository<ScholarshipSchemeEligibility, UUID> {

	Optional<ScholarshipSchemeEligibility> findByScholarshipScheme(ScholarshipScheme scholarshipScheme);
	
	List<ScholarshipSchemeEligibility> findByMinimumPercentageCriteriaLessThanEqualAndMaximumAnnualFamilyIncomeGreaterThanEqual(BigDecimal percentage,BigDecimal income);
}