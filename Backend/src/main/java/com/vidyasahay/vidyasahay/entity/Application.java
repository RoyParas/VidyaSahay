package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;

@Entity
@Table(name = "applications")
public class Application extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false, unique = false)
	private Student student;

	@Enumerated(EnumType.STRING)
	@Column(name = "application_type", nullable = false, length = 20)
	private ApplicationType applicationType;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "loan_scheme_id", nullable = true, unique = false)
	private LoanScheme loanScheme;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "scholarship_scheme_id", nullable = true, unique = false)
	private ScholarshipScheme scholarshipScheme;

	@Column(name = "approved_amount", precision = 15, scale = 2)
	private BigDecimal approvedAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 40)
	private ApplicationStatus status;

	@Column(name = "submitted_at")
	private LocalDateTime submittedAt;

	public Application() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	public LoanScheme getLoanScheme() {
		return loanScheme;
	}

	public void setLoanScheme(LoanScheme loanScheme) {
		this.loanScheme = loanScheme;
	}

	public ScholarshipScheme getScholarshipScheme() {
		return scholarshipScheme;
	}

	public void setScholarshipScheme(ScholarshipScheme scholarshipScheme) {
		this.scholarshipScheme = scholarshipScheme;
	}

	public BigDecimal getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(BigDecimal approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}
}
