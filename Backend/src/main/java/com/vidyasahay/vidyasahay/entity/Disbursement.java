package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.DisbursementStatus;

@Entity
@Table(name = "disbursements")
public class Disbursement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "application_id", nullable = false, unique = false)
	private Application application;

	@Enumerated(EnumType.STRING)
	@Column(name = "application_type", nullable = false, length = 20)
	private ApplicationType applicationType;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false, unique = false)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "disbursed_by_user_id", nullable = false, unique = false)
	private User disbursedByUser;

	@Column(name = "amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "disbursement_date", nullable = false)
	private LocalDate disbursementDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private DisbursementStatus status;

	@Column(name = "remark", length = 1000)
	private String remark;

	public Disbursement() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public ApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(ApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public User getDisbursedByUser() {
		return disbursedByUser;
	}

	public void setDisbursedByUser(User disbursedByUser) {
		this.disbursedByUser = disbursedByUser;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getDisbursementDate() {
		return disbursementDate;
	}

	public void setDisbursementDate(LocalDate disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	public DisbursementStatus getStatus() {
		return status;
	}

	public void setStatus(DisbursementStatus status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
