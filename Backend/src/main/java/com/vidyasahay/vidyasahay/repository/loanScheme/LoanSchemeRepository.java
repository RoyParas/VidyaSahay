package com.vidyasahay.vidyasahay.repository.loanScheme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.LoanSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.LoanSchemeRepaymentRule;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanSchemeRepository extends JpaRepository<LoanScheme, UUID> {

    List<LoanScheme> findByMaxLoanAmountGreaterThanEqualAndStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
      BigDecimal requiredLoanAmount,
      SchemeStatus status,
      LocalDate effectiveFrom,
      LocalDate effectiveTo
    );

    List<LoanScheme> findByStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
      SchemeStatus status,
      LocalDate effectiveFrom,
      LocalDate effectiveTo
    );

    List<LoanScheme> findByBankIdAndCreatedByIdOrderByCreatedAtDesc(UUID bankId, UUID createdByUserId);

    @Query("select eligibility from LoanSchemeEligibility eligibility where eligibility.loanScheme.id = :schemeId")
    Optional<LoanSchemeEligibility> findEligibilityBySchemeId(@Param("schemeId") UUID schemeId);

    @Query("select profession.id from LoanSchemeProfession mapping join mapping.profession profession where mapping.loanScheme.id = :schemeId")
    List<UUID> findEligibleProfessionIds(@Param("schemeId") UUID schemeId);

    @Query("select rule from LoanSchemeRepaymentRule rule where rule.loanScheme.id = :schemeId")
    Optional<LoanSchemeRepaymentRule> findRepaymentRuleBySchemeId(@Param("schemeId") UUID schemeId);

    @Query("select mapping.documentType.id from LoanSchemeRequiredDocument mapping where mapping.loanScheme.id = :schemeId")
    List<UUID> findRequiredDocumentTypeIds(@Param("schemeId") UUID schemeId);
}
