package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "scholarship_scheme_required_documents", uniqueConstraints = {@UniqueConstraint(name = "uq_scholarship_document", columnNames = { "scholarship_scheme_id","document_type_id" }) })
public class ScholarshipSchemeRequiredDocument extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "scholarship_scheme_id", nullable = false, unique = false)
	private ScholarshipScheme scholarshipScheme;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_type_id", nullable = false, unique = false)
	private DocumentType documentType;

	public ScholarshipSchemeRequiredDocument() {
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

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
}
