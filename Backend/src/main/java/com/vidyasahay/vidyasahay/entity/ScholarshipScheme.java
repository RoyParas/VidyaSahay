package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;

@Entity
@Table(name = "scholarship_schemes")
public class ScholarshipScheme extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "name", nullable = false, length = 200)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "scholarship_type", nullable = false, length = 30)
	private ScholarshipType scholarshipType;

	@Column(name = "academic_year", nullable = false, length = 20)
	private String academicYear;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private SchemeStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "created_by", nullable = false, unique = false)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "updated_by", nullable = true, unique = false)
	private User updatedBy;

	public ScholarshipScheme() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ScholarshipType getScholarshipType() {
		return scholarshipType;
	}

	public void setScholarshipType(ScholarshipType scholarshipType) {
		this.scholarshipType = scholarshipType;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public SchemeStatus getStatus() {
		return status;
	}

	public void setStatus(SchemeStatus status) {
		this.status = status;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
}
