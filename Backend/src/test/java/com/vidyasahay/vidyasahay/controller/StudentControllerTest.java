package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.CompleteStudentProfileRequest;
import com.vidyasahay.vidyasahay.enums.Gender;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.StudentService;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentController")
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private CompleteStudentProfileRequest request() {
        return new CompleteStudentProfileRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Andheri",
                400053,
                "123456789012",
                Gender.FEMALE,
                LocalDate.of(2003, 5, 17),
                "John Doe",
                "Mary Doe",
                new BigDecimal("300000.00"));
    }

    @Test
    @DisplayName("POST /api/students/complete-profile returns 200 and passes the principal's user id")
    void completeProfile_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
        CompleteStudentProfileRequest request = request();

        ResponseEntity<Void> response = studentController.completeProfile(principal, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(studentService).completeProfile(principal.getUserId(), request);
    }

    @Test
    @DisplayName("POST /api/students/complete-profile propagates an already-completed failure")
    void completeProfile_alreadyCompleted() {
        CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
        CompleteStudentProfileRequest request = request();

        doThrow(new BusinessException("Student profile has already been completed"))
                .when(studentService).completeProfile(principal.getUserId(), request);

        assertThatThrownBy(() -> studentController.completeProfile(principal, request))
                .isInstanceOf(BusinessException.class);
    }
}
