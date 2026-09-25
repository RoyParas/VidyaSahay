package com.vidyasahay.vidyasahay.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.StudentVerification;

@Repository
public interface StudentVerificationRepository
        extends JpaRepository<StudentVerification, UUID> {

    Optional<StudentVerification> findByStudentId(UUID studentId);
}
