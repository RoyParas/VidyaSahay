package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "scholarship_scheme_categories", uniqueConstraints = { @UniqueConstraint(name = "uq_scholarship_category", columnNames = { "scholarship_scheme_id", "category_id" }) })
public class ScholarshipSchemeCategory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "scholarship_scheme_id", nullable = false, unique = false)
	private ScholarshipScheme scholarshipScheme;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false, unique = false)
	private Category category;

	public ScholarshipSchemeCategory() {
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
