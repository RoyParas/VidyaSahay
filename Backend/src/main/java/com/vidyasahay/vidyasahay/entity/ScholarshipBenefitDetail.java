package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;

@Entity
@Table(name = "scholarship_benefit_details")
public class ScholarshipBenefitDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "scholarship_scheme_id", nullable = false, unique = true)
	private ScholarshipScheme scholarshipScheme;

	@Column(name = "scholarship_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal scholarshipAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "amount_type", nullable = false, length = 30)
	private ScholarshipAmountType amountType;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_frequency", nullable = false, length = 20)
	private PaymentFrequency paymentFrequency;

	@Column(name = "total_scheme_budget", nullable = false, precision = 18, scale = 2)
	private BigDecimal totalSchemeBudget;

	public ScholarshipBenefitDetail() {
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

	public BigDecimal getScholarshipAmount() {
		return scholarshipAmount;
	}

	public void setScholarshipAmount(BigDecimal scholarshipAmount) {
		this.scholarshipAmount = scholarshipAmount;
	}

	public ScholarshipAmountType getAmountType() {
		return amountType;
	}

	public void setAmountType(ScholarshipAmountType amountType) {
		this.amountType = amountType;
	}

	public PaymentFrequency getPaymentFrequency() {
		return paymentFrequency;
	}

	public void setPaymentFrequency(PaymentFrequency paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	public BigDecimal getTotalSchemeBudget() {
		return totalSchemeBudget;
	}

	public void setTotalSchemeBudget(BigDecimal totalSchemeBudget) {
		this.totalSchemeBudget = totalSchemeBudget;
	}
}
