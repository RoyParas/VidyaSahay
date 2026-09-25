package com.vidyasahay.vidyasahay.repository.loanScheme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanSchemeRepository extends JpaRepository<LoanScheme, UUID> {

    List<LoanScheme> findByMaxLoanAmountGreaterThanEqualAndStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
      BigDecimal requiredLoanAmount,
      SchemeStatus status,
      LocalDate effectiveFrom,
      LocalDate effectiveTo
    );

    List<LoanScheme> findByBankIdAndCreatedByIdOrderByCreatedAtDesc(UUID bankId, UUID createdByUserId);
}
