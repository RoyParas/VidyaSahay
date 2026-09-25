package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

@Entity
@Table(name = "student_verifications")
public class StudentVerification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false, unique = true)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "institute_id", nullable = false, unique = false)
	private Institute institute;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private VerificationStatus status;

	@Column(name = "remark", length = 1000)
	private String remark;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "verified_by", nullable = true, unique = false)
	private User verifiedBy;

	@Column(name = "verified_at")
	private LocalDateTime verifiedAt;

	public StudentVerification() {
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

	public Institute getInstitute() {
		return institute;
	}

	public void setInstitute(Institute institute) {
		this.institute = institute;
	}

	public VerificationStatus getStatus() {
		return status;
	}

	public void setStatus(VerificationStatus status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(User verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public LocalDateTime getVerifiedAt() {
		return verifiedAt;
	}

	public void setVerifiedAt(LocalDateTime verifiedAt) {
		this.verifiedAt = verifiedAt;
	}
}
