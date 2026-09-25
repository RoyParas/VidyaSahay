package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.Student;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

	/*
	 * STUDENT: applications.student.user.id = authenticated user ID
	 */
	@EntityGraph(attributePaths = { "student", "student.user", "loanScheme", "scholarshipScheme" })
	List<Application> findAllByStudentUserIdOrderBySubmittedAtDesc(UUID userId);

	/*
	 * BANK: applications.loanScheme.createdBy.id = authenticated user ID
	 */
	@EntityGraph(attributePaths = { "student", "student.user", "loanScheme" })
	List<Application> findAllByLoanSchemeCreatedByIdOrderBySubmittedAtDesc(UUID userId);

	/*
	 * GOVERNMENT: applications.scholarshipScheme.createdBy.id = authenticated user
	 * ID
	 */
	@EntityGraph(attributePaths = { "student", "student.user", "scholarshipScheme" })
	List<Application> findAllByScholarshipSchemeCreatedByIdOrderBySubmittedAtDesc(UUID userId);

	/*
	 * INSTITUTE: applications.student.institute.user.id = authenticated user ID
	 */
	@EntityGraph(attributePaths = { "student", "student.user", "student.institute", "loanScheme", "scholarshipScheme" })
	List<Application> findAllByStudentInstituteUserIdOrderBySubmittedAtDesc(UUID userId);

	/*
	 * ADMIN: Return every application
	 */
	@EntityGraph(attributePaths = { "student", "student.user", "student.institute", "loanScheme", "scholarshipScheme" })
	List<Application> findAllByOrderBySubmittedAtDesc();

	@EntityGraph(attributePaths = { "student", "student.user", "student.institute", "student.address", "student.course",
			"student.category", "loanScheme", "scholarshipScheme" })
	Optional<Application> findApplicationById(UUID id);

	@EntityGraph(attributePaths = { "student", "student.user", "loanScheme", "loanScheme.createdBy" })
	Optional<Application> findByIdAndLoanSchemeCreatedById(UUID applicationId, UUID userId);

	@EntityGraph(attributePaths = { "student", "student.user", "scholarshipScheme", "scholarshipScheme.createdBy" })
	Optional<Application> findByIdAndScholarshipSchemeCreatedById(UUID applicationId, UUID userId);
}
