package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "loan_scheme_moratoriums")
public class LoanSchemeMoratorium extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "loan_scheme_id", nullable = false, unique = true)
	private LoanScheme loanScheme;

	@Column(name = "course_period_included", nullable = false)
	private boolean coursePeriodIncluded;

	@Column(name = "additional_months", nullable = false)
	private Integer additionalMonths;

	public LoanSchemeMoratorium() {
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

	public boolean isCoursePeriodIncluded() {
		return coursePeriodIncluded;
	}

	public void setCoursePeriodIncluded(boolean coursePeriodIncluded) {
		this.coursePeriodIncluded = coursePeriodIncluded;
	}

	public Integer getAdditionalMonths() {
		return additionalMonths;
	}

	public void setAdditionalMonths(Integer additionalMonths) {
		this.additionalMonths = additionalMonths;
	}
}
