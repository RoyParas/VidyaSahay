package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

@Entity
@Table(name = "application_documents", uniqueConstraints = {
		@UniqueConstraint(name = "uq_application_student_document", columnNames = { "application_id",
				"student_document_id" }) })
public class ApplicationDocument extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "application_id", nullable = false, unique = false)
	private Application application;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_document_id", nullable = false, unique = false)
	private StudentDocument studentDocument;

	public ApplicationDocument() {
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

	public StudentDocument getStudentDocument() {
		return studentDocument;
	}

	public void setStudentDocument(StudentDocument studentDocument) {
		this.studentDocument = studentDocument;
	}

}
