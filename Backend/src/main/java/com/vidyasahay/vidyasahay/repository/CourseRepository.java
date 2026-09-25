package com.vidyasahay.vidyasahay.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    List<Course> findAllByInstitute_Id(UUID instituteId);

    boolean existsByInstitute_IdAndNameIgnoreCase(
            UUID instituteId,
            String name
    );

    boolean existsByInstitute_IdAndNameIgnoreCaseAndIdNot(
            UUID instituteId,
            String name,
            UUID courseId
    );

    Optional<Course> findOneById(UUID courseId);

}