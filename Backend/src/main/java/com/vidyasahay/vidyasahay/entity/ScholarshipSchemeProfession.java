package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "scholarship_scheme_professions", uniqueConstraints = { @UniqueConstraint(name = "uq_scholarship_profession", columnNames = { "scholarship_scheme_id", "profession_id" }) })
public class ScholarshipSchemeProfession extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "scholarship_scheme_id", nullable = false, unique = false)
	private ScholarshipScheme scholarshipScheme;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "profession_id", nullable = false, unique = false)
	private Profession profession;

	public ScholarshipSchemeProfession() {
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

	public Profession getProfession() {
		return profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}
}
