package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "courses", uniqueConstraints = {
		@UniqueConstraint(name = "uq_course_institute_name", columnNames = { "institute_id", "name" }) })
public class Course extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "institute_id", nullable = false, unique = false)
	private Institute institute;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "profession_id", nullable = false, unique = false)
	private Profession profession;

	@Column(name = "name", nullable = false, length = 200)
	private String name;

	@Column(name = "duration_years", nullable = false)
	private Integer durationYears;

	@Column(name = "fees", nullable = false, precision = 15, scale = 2)
	private BigDecimal fees;

	public Course() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Institute getInstitute() {
		return institute;
	}

	public void setInstitute(Institute institute) {
		this.institute = institute;
	}

	public Profession getProfession() {
		return profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDurationYears() {
		return durationYears;
	}

	public void setDurationYears(Integer durationYears) {
		this.durationYears = durationYears;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}
}
