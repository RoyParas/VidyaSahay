package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "loan_scheme_required_documents", uniqueConstraints = {@UniqueConstraint(name = "uq_loan_scheme_document", columnNames = { "loan_scheme_id", "document_type_id" }) })
public class LoanSchemeRequiredDocument extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "loan_scheme_id", nullable = false, unique = false)
	private LoanScheme loanScheme;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_type_id", nullable = false, unique = false)
	private DocumentType documentType;

	public LoanSchemeRequiredDocument() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LoanScheme getLoanScheme() {
		return loanScheme;
	}

	public void setLoanScheme(LoanScheme loanScheme) {
		this.loanScheme = loanScheme;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
}
