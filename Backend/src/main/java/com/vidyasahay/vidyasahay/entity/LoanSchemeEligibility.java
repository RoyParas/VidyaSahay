package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "loan_scheme_eligibility")
public class LoanSchemeEligibility extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "loan_scheme_id", nullable = false, unique = true)
	private LoanScheme loanScheme;

	@Column(name = "min_age", nullable = false)
	private Integer minAge;

	@Column(name = "max_age", nullable = false)
	private Integer maxAge;

	@Column(name = "co_borrower_required", nullable = false)
	private boolean coBorrowerRequired;

	@Column(name = "min_credit_score")
	private Integer minCreditScore;

	public LoanSchemeEligibility() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LoanScheme getLoanScheme() {
		return loanScheme;
	}

	public void setLoanScheme(LoanScheme loanScheme) {
		this.loanScheme = loanScheme;
	}

	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public boolean isCoBorrowerRequired() {
		return coBorrowerRequired;
	}

	public void setCoBorrowerRequired(boolean coBorrowerRequired) {
		this.coBorrowerRequired = coBorrowerRequired;
	}

	public Integer getMinCreditScore() {
		return minCreditScore;
	}

	public void setMinCreditScore(Integer minCreditScore) {
		this.minCreditScore = minCreditScore;
	}
}
