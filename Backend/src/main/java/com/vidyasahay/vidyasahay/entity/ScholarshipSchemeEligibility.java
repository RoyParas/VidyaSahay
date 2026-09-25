package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "scholarship_scheme_eligibility")
public class ScholarshipSchemeEligibility extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "scholarship_scheme_id", nullable = false, unique = true)
	private ScholarshipScheme scholarshipScheme;

	@Column(name = "minimum_age")
	private Integer minimumAge;

	@Column(name = "maximum_age")
	private Integer maximumAge;

	@Column(name = "maximum_annual_family_income", precision = 15, scale = 2)
	private BigDecimal maximumAnnualFamilyIncome;

	@Column(name = "minimum_percentage_criteria", precision = 5, scale = 2)
	private BigDecimal minimumPercentageCriteria;

	public ScholarshipSchemeEligibility() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ScholarshipScheme getScholarshipScheme() {
		return scholarshipScheme;
	}

	public void setScholarshipScheme(ScholarshipScheme scholarshipScheme) {
		this.scholarshipScheme = scholarshipScheme;
	}

	public Integer getMinimumAge() {
		return minimumAge;
	}

	public void setMinimumAge(Integer minimumAge) {
		this.minimumAge = minimumAge;
	}

	public Integer getMaximumAge() {
		return maximumAge;
	}

	public void setMaximumAge(Integer maximumAge) {
		this.maximumAge = maximumAge;
	}

	public BigDecimal getMaximumAnnualFamilyIncome() {
		return maximumAnnualFamilyIncome;
	}

	public void setMaximumAnnualFamilyIncome(BigDecimal maximumAnnualFamilyIncome) {
		this.maximumAnnualFamilyIncome = maximumAnnualFamilyIncome;
	}

	public BigDecimal getMinimumPercentageCriteria() {
		return minimumPercentageCriteria;
	}

	public void setMinimumPercentageCriteria(BigDecimal minimumPercentageCriteria) {
		this.minimumPercentageCriteria = minimumPercentageCriteria;
	}
}
