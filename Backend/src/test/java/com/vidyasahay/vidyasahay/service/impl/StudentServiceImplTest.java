package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.CompleteStudentProfileRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentSummaryResponse;
import com.vidyasahay.vidyasahay.entity.Address;
import com.vidyasahay.vidyasahay.entity.Category;
import com.vidyasahay.vidyasahay.entity.Course;
import com.vidyasahay.vidyasahay.entity.Institute;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.Gender;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.AddressRepository;
import com.vidyasahay.vidyasahay.repository.CategoryRepository;
import com.vidyasahay.vidyasahay.repository.CourseRepository;
import com.vidyasahay.vidyasahay.repository.InstituteRepository;
import com.vidyasahay.vidyasahay.repository.StudentRepository;
import com.vidyasahay.vidyasahay.repository.StudentVerificationRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentServiceImpl")
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentVerificationRepository studentVerificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Nested
    @DisplayName("getAllStudents")
    class GetAllStudents {

        @Test
        @DisplayName("maps every student to a summary with a masked Aadhaar number")
        void getAllStudents_success() {
            Student student = TestData.student();
            StudentVerification studentVerification = TestData.verification(student,VerificationStatus.VERIFIED);

            when(studentRepository.findAllBy()).thenReturn(List.of(student));

            List<StudentSummaryResponse> response = studentService.getAllStudents();

            assertThat(response).hasSize(1);
            assertThat(response.get(0).studentId()).isEqualTo(student.getId());
            assertThat(response.get(0).email()).isEqualTo(student.getUser().getEmail());
            assertThat(response.get(0).courseName()).isEqualTo(student.getCourse().getName());
            assertThat(response.get(0).instituteName()).isEqualTo(student.getInstitute().getName());
        }

        @Test
        @DisplayName("fails when no students exist")
        void getAllStudents_empty() {
            when(studentRepository.findAllBy()).thenReturn(List.of());

            assertThatThrownBy(() -> studentService.getAllStudents())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No students found");
        }
    }

    @Nested
    @DisplayName("getStudentById")
    class GetStudentById {

        @Test
        @DisplayName("returns the stored verification status when one exists")
        void getStudentById_withVerification() {
            Student student = TestData.student();
            StudentVerification verification =
                    TestData.verification(student, VerificationStatus.VERIFIED);

            when(studentRepository.findOneById(student.getId())).thenReturn(Optional.of(student));
            when(studentVerificationRepository.findByStudentId(student.getId()))
                    .thenReturn(Optional.of(verification));

            StudentDetailedResponse response = studentService.getStudentById(student.getId());

            assertThat(response.studentId()).isEqualTo(student.getId());
            assertThat(response.userId()).isEqualTo(student.getUser().getId());
            assertThat(response.verificationStatus()).isEqualTo(VerificationStatus.VERIFIED);
            assertThat(response.profileCompleted()).isTrue();
            assertThat(response.maskedAadharNumber()).isEqualTo("XXXXXXXX9012");
            assertThat(response.address().city()).isEqualTo("Mumbai");
        }

        @Test
        @DisplayName("falls back to PENDING when no verification row exists")
        void getStudentById_withoutVerification() {
            Student student = TestData.student();

            when(studentRepository.findOneById(student.getId())).thenReturn(Optional.of(student));
            when(studentVerificationRepository.findByStudentId(student.getId()))
                    .thenReturn(Optional.empty());

            assertThat(studentService.getStudentById(student.getId()).verificationStatus())
                    .isEqualTo(VerificationStatus.PENDING);
        }

        @Test
        @DisplayName("fails for an unknown student id")
        void getStudentById_notFound() {
            UUID studentId = UUID.randomUUID();

            when(studentRepository.findOneById(studentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.getStudentById(studentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student not found");
        }
    }

    @Nested
    @DisplayName("completeProfile")
    class CompleteProfile {

        private UUID userId;
        private User user;
        private Institute institute;
        private Course course;
        private Category category;
        private Address address;
        private CompleteStudentProfileRequest request;

        @BeforeEach
        void setUp() {
            user = TestData.user(RoleName.STUDENT);
            userId = user.getId();
            institute = TestData.institute();
            course = TestData.course(institute);
            category = TestData.category();
            address = TestData.address();

            request = new CompleteStudentProfileRequest(
                    institute.getId(),
                    course.getId(),
                    category.getId(),
                    address.getId(),
                    "  Andheri  ",
                    400053,
                    " 123456789012 ",
                    Gender.FEMALE,
                    LocalDate.of(2003, 5, 17),
                    "  John Doe  ",
                    "  Mary Doe  ",
                    new BigDecimal("300000.00"));
        }

        private void stubHappyPath() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(courseRepository.findOneById(course.getId())).thenReturn(Optional.of(course));
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        }

        @Test
        @DisplayName("saves a trimmed student profile and a PENDING verification row")
        void completeProfile_success() {
            stubHappyPath();
            when(studentRepository.save(any(Student.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            studentService.completeProfile(userId, request);

            ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(studentCaptor.capture());

            Student saved = studentCaptor.getValue();
            assertThat(saved.getUser()).isSameAs(user);
            assertThat(saved.getInstitute()).isSameAs(institute);
            assertThat(saved.getCourse()).isSameAs(course);
            assertThat(saved.getCategory()).isSameAs(category);
            assertThat(saved.getAddress()).isSameAs(address);
            assertThat(saved.getLocation()).isEqualTo("Andheri");
            assertThat(saved.getAadharNumber()).isEqualTo("123456789012");
            assertThat(saved.getFatherName()).isEqualTo("John Doe");
            assertThat(saved.getMotherName()).isEqualTo("Mary Doe");

            ArgumentCaptor<StudentVerification> verificationCaptor =
                    ArgumentCaptor.forClass(StudentVerification.class);
            verify(studentVerificationRepository).save(verificationCaptor.capture());

            StudentVerification verification = verificationCaptor.getValue();
            assertThat(verification.getStatus()).isEqualTo(VerificationStatus.PENDING);
            assertThat(verification.getInstitute()).isSameAs(institute);
            assertThat(verification.getRemark()).isNull();
            assertThat(verification.getVerifiedBy()).isNull();
            assertThat(verification.getVerifiedAt()).isNull();
        }

        @Test
        @DisplayName("keeps a null location as null")
        void completeProfile_nullLocation() {
            CompleteStudentProfileRequest nullLocation = new CompleteStudentProfileRequest(
                    institute.getId(), course.getId(), category.getId(), address.getId(),
                    null, 400053, "123456789012", Gender.MALE, LocalDate.of(2003, 5, 17),
                    "John", "Mary", BigDecimal.ZERO);

            stubHappyPath();
            when(studentRepository.save(any(Student.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            studentService.completeProfile(userId, nullLocation);

            ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
            verify(studentRepository).save(captor.capture());

            assertThat(captor.getValue().getLocation()).isNull();
        }

        @Test
        @DisplayName("refuses to complete an already completed profile")
        void completeProfile_alreadyCompleted() {
            when(studentRepository.existsByUserId(userId)).thenReturn(true);

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("already been completed");

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects an Aadhaar number that is already on file")
        void completeProfile_duplicateAadhaar() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(true);

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Aadhaar");

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("fails when the user row is missing")
        void completeProfile_userNotFound() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }

        @Test
        @DisplayName("fails for an unknown institute")
        void completeProfile_instituteNotFound() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Institute not found");
        }

        @Test
        @DisplayName("fails for an unknown course")
        void completeProfile_courseNotFound() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(courseRepository.findOneById(course.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Course not found");
        }

        @Test
        @DisplayName("rejects a course that belongs to a different institute")
        void completeProfile_courseFromAnotherInstitute() {
            Course foreignCourse = TestData.course(TestData.institute());

            CompleteStudentProfileRequest foreignRequest = new CompleteStudentProfileRequest(
                    institute.getId(), foreignCourse.getId(), category.getId(), address.getId(),
                    "Andheri", 400053, "123456789012", Gender.MALE, LocalDate.of(2003, 5, 17),
                    "John", "Mary", BigDecimal.ZERO);

            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(courseRepository.findOneById(foreignCourse.getId()))
                    .thenReturn(Optional.of(foreignCourse));

            assertThatThrownBy(() -> studentService.completeProfile(userId, foreignRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("does not belong");

            verify(studentRepository, never()).save(any());
        }

        @Test
        @DisplayName("fails for an unknown category")
        void completeProfile_categoryNotFound() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(courseRepository.findOneById(course.getId())).thenReturn(Optional.of(course));
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category not found");
        }

        @Test
        @DisplayName("fails for an unknown address")
        void completeProfile_addressNotFound() {
            when(studentRepository.existsByUserId(userId)).thenReturn(false);
            when(studentRepository.existsByAadharNumber("123456789012")).thenReturn(false);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(courseRepository.findOneById(course.getId())).thenReturn(Optional.of(course));
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> studentService.completeProfile(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Address not found");

            verify(studentRepository, never()).save(any());
        }
    }
}
