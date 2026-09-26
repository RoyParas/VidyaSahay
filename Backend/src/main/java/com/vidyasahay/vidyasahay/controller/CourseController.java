package com.vidyasahay.vidyasahay.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.request.CourseRequest;
import com.vidyasahay.vidyasahay.dto.response.CourseResponse;
import com.vidyasahay.vidyasahay.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

        private final CourseService courseService;

        public CourseController(CourseService courseService) {
                this.courseService = courseService;
        }

        /*
         * Get all courses
         *
         * Method: GET
         * URL: /api/courses
         */
        @PreAuthorize("hasAnyRole('INSTITUTE' ,'BANK','ADMIN','GOVERNMENT','STUDENT')")
        @GetMapping
        public ResponseEntity<List<CourseResponse>> getAllCourses() {

                List<CourseResponse> courses = courseService.getAllCourses();

                return ResponseEntity.ok(courses);
        }

        /*
         * Get course by ID
         *
         * Method: GET
         * URL: /api/courses/{courseId}
         */
        @PreAuthorize("hasRole('INSTITUTE')")
        @GetMapping("/{courseId}")
        public ResponseEntity<CourseResponse> getCourseById(
                        @PathVariable UUID courseId) {

                CourseResponse course = courseService.getCourseById(courseId);

                return ResponseEntity.ok(course);
        }

        /*
         * Create course
         *
         * Method: POST
         * URL: /api/courses
         */
        @PreAuthorize("hasRole('INSTITUTE')")
        @PostMapping
        public ResponseEntity<CourseResponse> createCourse(
                        @Valid @RequestBody CourseRequest request) {

                CourseResponse createdCourse = courseService.createCourse(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(createdCourse);
        }

        /*
         * Update course
         *
         * Method: PUT
         * URL: /api/courses/{courseId}
         */
        @PreAuthorize("hasRole('INSTITUTE')")
        @PatchMapping("/{courseId}")
        public ResponseEntity<CourseResponse> updateCourse(
                        @PathVariable UUID courseId,
                        @Valid @RequestBody CourseRequest request) {

                CourseResponse updatedCourse = courseService.updateCourse(courseId, request);

                return ResponseEntity.ok(updatedCourse);
        }

        /*
         * Get courses by institute ID
         *
         * Method: GET
         * URL: /api/courses/institute/{instituteId}
         */
        @PreAuthorize("hasAnyRole('INSTITUTE', 'STUDENT')")
        @GetMapping("/institute/{instituteId}")
        public ResponseEntity<List<CourseResponse>> getCoursesByInstituteId(
                        @PathVariable UUID instituteId) {

                List<CourseResponse> courses = courseService.getCoursesByInstituteId(instituteId);

                return ResponseEntity.ok(courses);
        }

        /*
         * Delete course
         *
         * Method: DELETE
         * URL: /api/courses/{courseId}
         */
        @PreAuthorize("hasRole('INSTITUTE')")
        @DeleteMapping("/{courseId}")
        public ResponseEntity<Void> deleteCourse(
                        @PathVariable UUID courseId) {

                courseService.deleteCourse(courseId);

                return ResponseEntity.noContent().build();
        }
}
