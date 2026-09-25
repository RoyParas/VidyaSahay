package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.VerificationStatus;

@Entity
@Table(name = "student_documents")
public class StudentDocument extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false, unique = false)
	private Student student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_type_id", nullable = false, unique = false)
	private DocumentType documentType;

	@Column(name = "file_name", nullable = false, length = 255)
	private String fileName;

	@Column(name = "file_path", nullable = false, length = 1000)
	private String filePath;

	@Enumerated(EnumType.STRING)
	@Column(name = "verification_status", nullable = false, length = 20)
	private VerificationStatus verificationStatus;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "verified_by", nullable = true, unique = false)
	private User verifiedBy;

	@Column(name = "verified_at")
	private LocalDateTime verifiedAt;

	public StudentDocument() {
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

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public VerificationStatus getVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(VerificationStatus verificationStatus) {
		this.verificationStatus = verificationStatus;
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
