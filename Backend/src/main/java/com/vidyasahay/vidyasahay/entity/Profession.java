package com.vidyasahay.vidyasahay.entity;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "professions")
public class Profession extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "name", nullable = false, unique = true, length = 150)
	private String name;

	public Profession() {
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
}
