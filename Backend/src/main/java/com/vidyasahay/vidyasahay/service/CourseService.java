package com.vidyasahay.vidyasahay.service;

import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.CourseRequest;
import com.vidyasahay.vidyasahay.dto.response.CourseResponse;

public interface CourseService {

    List<CourseResponse> getAllCourses();

    CourseResponse getCourseById(UUID courseId);

    CourseResponse createCourse(CourseRequest request);

    CourseResponse updateCourse(
            UUID courseId,
            CourseRequest request
    );

    List<CourseResponse> getCoursesByInstituteId(UUID instituteId);

    void deleteCourse(UUID courseId);
}