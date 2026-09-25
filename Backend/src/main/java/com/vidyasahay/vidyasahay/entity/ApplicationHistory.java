package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.ApplicationStatus;

@Entity
@Table(name = "application_history")
public class ApplicationHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "application_id", nullable = false, unique = false)
	private Application application;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 40)
	private ApplicationStatus status;

	@Column(name = "remark", length = 1000)
	private String remark;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "action_by_user_id", nullable = false, unique = false)
	private User actionByUser;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "assigned_to_user_id", nullable = true, unique = false)
	private User assignedToUser;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public ApplicationHistory() {
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

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getActionByUser() {
		return actionByUser;
	}

	public void setActionByUser(User actionByUser) {
		this.actionByUser = actionByUser;
	}

	public User getAssignedToUser() {
		return assignedToUser;
	}

	public void setAssignedToUser(User assignedToUser) {
		this.assignedToUser = assignedToUser;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
