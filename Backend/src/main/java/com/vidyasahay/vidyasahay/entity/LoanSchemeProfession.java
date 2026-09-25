package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "loan_scheme_professions", uniqueConstraints = {@UniqueConstraint(name = "uq_loan_scheme_profession", columnNames = { "loan_scheme_id", "profession_id" }) })
public class LoanSchemeProfession extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "loan_scheme_id", nullable = false, unique = false)
	private LoanScheme loanScheme;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "profession_id", nullable = false, unique = false)
	private Profession profession;

	public LoanSchemeProfession() {
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

	public Profession getProfession() {
		return profession;
	}

	public void setProfession(Profession profession) {
		this.profession = profession;
	}
}
