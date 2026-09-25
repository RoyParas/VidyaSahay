package com.vidyasahay.vidyasahay.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.CourseRequest;
import com.vidyasahay.vidyasahay.dto.response.CourseResponse;
import com.vidyasahay.vidyasahay.entity.Course;
import com.vidyasahay.vidyasahay.entity.Institute;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.CourseRepository;
import com.vidyasahay.vidyasahay.repository.InstituteRepository;
import com.vidyasahay.vidyasahay.repository.ProfessionRepository;
import com.vidyasahay.vidyasahay.service.CourseService;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

        private final CourseRepository courseRepository;
        private final InstituteRepository instituteRepository;
        private final ProfessionRepository professionRepository;

        public CourseServiceImpl(
                        CourseRepository courseRepository,
                        InstituteRepository instituteRepository,
                        ProfessionRepository professionRepository) {

                this.courseRepository = courseRepository;
                this.instituteRepository = instituteRepository;
                this.professionRepository = professionRepository;
        }

        @Override
        @Transactional(readOnly = true)
        public List<CourseResponse> getAllCourses() {

                return courseRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public CourseResponse getCourseById(UUID courseId) {

                Course course = findCourseById(courseId);

                return mapToResponse(course);
        }

        @Override
        public CourseResponse createCourse(CourseRequest request) {

                String courseName = request.getName().trim();

                validateDuplicateCourse(
                                request.getInstituteId(),
                                courseName,
                                null);

                Institute institute = findInstituteById(
                                request.getInstituteId());

                Profession profession = findProfessionById(
                                request.getProfessionId());

                Course course = new Course();

                course.setInstitute(institute);
                course.setProfession(profession);
                course.setName(courseName);
                course.setDurationYears(request.getDurationYears());
                course.setFees(request.getFees());

                Course savedCourse = courseRepository.save(course);

                return mapToResponse(savedCourse);
        }

        @Override
        public CourseResponse updateCourse(
                        UUID courseId,
                        CourseRequest request) {

                Course existingCourse = findCourseById(courseId);

                if (request.getInstituteId() != null) {

                        Institute institute = findInstituteById(request.getInstituteId());

                        existingCourse.setInstitute(institute);
                }

                if (request.getProfessionId() != null) {

                        Profession profession = findProfessionById(request.getProfessionId());

                        existingCourse.setProfession(profession);
                }

                if (request.getName() != null) {

                        String courseName = request.getName().trim();

                        if (courseName.isBlank()) {
                                throw new IllegalArgumentException(
                                                "Course name cannot be blank");
                        }

                        UUID instituteId = existingCourse.getInstitute().getId();

                        validateDuplicateCourse(
                                        instituteId,
                                        courseName,
                                        courseId);

                        existingCourse.setName(courseName);
                }

                if (request.getDurationYears() != null) {
                        existingCourse.setDurationYears(
                                        request.getDurationYears());
                }

                if (request.getFees() != null) {
                        existingCourse.setFees(request.getFees());
                }

                Course updatedCourse = courseRepository.save(existingCourse);

                return mapToResponse(updatedCourse);
        }

        @Override
        @Transactional(readOnly = true)
        public List<CourseResponse> getCoursesByInstituteId(
                        UUID instituteId) {

                if (!instituteRepository.existsById(instituteId)) {
                        throw new ResourceNotFoundException(
                                        "Institute not found with ID: " + instituteId);
                }

                return courseRepository.findAllByInstitute_Id(instituteId)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public void deleteCourse(UUID courseId) {

                Course course = findCourseById(courseId);

                try {
                        courseRepository.delete(course);

                        /*
                         * Executes the DELETE query immediately so that
                         * the foreign-key error occurs inside this try block.
                         */
                        courseRepository.flush();

                } catch (DataIntegrityViolationException exception) {

                        throw new BusinessException(
                                        "Course cannot be deleted because it is already "
                                                        + "assigned to one or*more students.");
                }
        }

        private Course findCourseById(UUID courseId) {

                return courseRepository.findById(courseId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Course not found with ID: " + courseId));
        }

        private Institute findInstituteById(UUID instituteId) {

                return instituteRepository.findById(instituteId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Institute not found with ID: "
                                                                + instituteId));
        }

        private Profession findProfessionById(UUID professionId) {

                return professionRepository.findById(professionId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Profession not found with ID: "
                                                                + professionId));
        }

        private void validateDuplicateCourse(
                        UUID instituteId,
                        String courseName,
                        UUID courseId) {

                boolean courseExists;

                if (courseId == null) {
                        courseExists = courseRepository
                                        .existsByInstitute_IdAndNameIgnoreCase(
                                                        instituteId,
                                                        courseName);
                } else {
                        courseExists = courseRepository
                                        .existsByInstitute_IdAndNameIgnoreCaseAndIdNot(
                                                        instituteId,
                                                        courseName,
                                                        courseId);
                }

                if (courseExists) {
                        throw new IllegalArgumentException(
                                        "Course with name '" + courseName
                                                        + "' already exists for this institute");
                }
        }

        private CourseResponse mapToResponse(Course course) {

                CourseResponse response = new CourseResponse();

                response.setId(course.getId());
                response.setName(course.getName());
                response.setDurationYears(course.getDurationYears());
                response.setFees(course.getFees());

                if (course.getInstitute() != null) {
                        response.setInstituteId(
                                        course.getInstitute().getId());

                        response.setInstituteName(
                                        course.getInstitute().getName());
                }

                if (course.getProfession() != null) {
                        response.setProfessionId(
                                        course.getProfession().getId());

                        response.setProfessionName(
                                        course.getProfession().getName());
                }

                return response;
        }
}