package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loan_scheme_repayment_rules")
public class LoanSchemeRepaymentRule extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "loan_scheme_id", nullable = false, unique = true)
	private LoanScheme loanScheme;

	@Column(name = "min_tenure_years", nullable = false)
	private Integer minTenureYears;

	@Column(name = "max_tenure_years", nullable = false)
	private Integer maxTenureYears;

	@Column(name = "prepayment_allowed", nullable = false)
	private boolean prepaymentAllowed;

	@Column(name = "foreclosure_charges", precision = 10, scale = 2)
	private BigDecimal foreclosureCharges;

	public LoanSchemeRepaymentRule() {
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

	public Integer getMinTenureYears() {
		return minTenureYears;
	}

	public void setMinTenureYears(Integer minTenureYears) {
		this.minTenureYears = minTenureYears;
	}

	public Integer getMaxTenureYears() {
		return maxTenureYears;
	}

	public void setMaxTenureYears(Integer maxTenureYears) {
		this.maxTenureYears = maxTenureYears;
	}

	public boolean isPrepaymentAllowed() {
		return prepaymentAllowed;
	}

	public void setPrepaymentAllowed(boolean prepaymentAllowed) {
		this.prepaymentAllowed = prepaymentAllowed;
	}

	public BigDecimal getForeclosureCharges() {
		return foreclosureCharges;
	}

	public void setForeclosureCharges(BigDecimal foreclosureCharges) {
		this.foreclosureCharges = foreclosureCharges;
	}
}
