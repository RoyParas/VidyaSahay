package com.vidyasahay.vidyasahay.repository.loanScheme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanSchemeRepaymentRule;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanSchemeRepaymentRuleRepository extends JpaRepository<LoanSchemeRepaymentRule, UUID> {

    Optional<LoanSchemeRepaymentRule> findByLoanSchemeId(UUID loanSchemeId);
}