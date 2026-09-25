package com.vidyasahay.vidyasahay.repository.loanScheme;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanSchemeEligibility;

@Repository
public interface LoanSchemeEligibilityRepository extends JpaRepository<LoanSchemeEligibility, UUID> {
	Optional<LoanSchemeEligibility> findByLoanSchemeId(UUID loanSchemeId);
}