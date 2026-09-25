package com.vidyasahay.vidyasahay.repository.scholarshipScheme;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.ScholarshipBenefitDetail;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;

@Repository
public interface ScholarshipBenefitDetailRepository extends JpaRepository<ScholarshipBenefitDetail, UUID> {

	Optional<ScholarshipBenefitDetail> findByScholarshipScheme(ScholarshipScheme scholarshipScheme);
}