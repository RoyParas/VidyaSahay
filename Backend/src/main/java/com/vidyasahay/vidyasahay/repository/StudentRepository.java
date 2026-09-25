package com.vidyasahay.vidyasahay.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    /*
     * Finds a student profile using the logged-in user's ID.
     * Required for complete-profile and student self-service operations.
     */
    @EntityGraph(attributePaths = {
            "user",
            "institute",
            "address",
            "category",
            "course",
            "course.profession"
    })
    Optional<Student> findByUserId(UUID userId);

    /*
     * Fetches all students with the relationships required
     * for StudentSummaryResponse.
     */
    @EntityGraph(attributePaths = {
            "user",
            "institute",
            "course"
    })
    List<Student> findAllBy();

    @EntityGraph(attributePaths = {
            "user",
            "institute",
            "course"
    })
    List<Student> findAllByInstituteId(UUID instituteId);

    /*
     * Fetches one student with all relationships required
     * for StudentDetailedResponse.
     */
    @EntityGraph(attributePaths = {
            "user",
            "institute",
            "address",
            "category",
            "course",
            "course.profession"
    })
    Optional<Student> findOneById(UUID studentId);

    /*
     * Checks whether the logged-in user has already
     * completed a student profile.
     */
    boolean existsByUserId(UUID userId);

    /*
     * Prevents the same Aadhaar number from being
     * associated with multiple students.
     */
    boolean existsByAadharNumber(String aadharNumber);
}