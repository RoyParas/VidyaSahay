package com.vidyasahay.vidyasahay.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.CompleteStudentProfileRequest;
import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.CategoryResponse;
import com.vidyasahay.vidyasahay.dto.response.CourseSummaryResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteSummaryResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentSummaryResponse;
import com.vidyasahay.vidyasahay.entity.Address;
import com.vidyasahay.vidyasahay.entity.Category;
import com.vidyasahay.vidyasahay.entity.Course;
import com.vidyasahay.vidyasahay.entity.Institute;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.entity.User;
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
import com.vidyasahay.vidyasahay.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

        private final StudentRepository studentRepository;
        private final StudentVerificationRepository studentVerificationRepository;
        private final UserRepository userRepository;
        private final InstituteRepository instituteRepository;
        private final CourseRepository courseRepository;
        private final CategoryRepository categoryRepository;
        private final AddressRepository addressRepository;

        public StudentServiceImpl(
                        StudentRepository studentRepository,
                        StudentVerificationRepository studentVerificationRepository,
                        UserRepository userRepository,
                        InstituteRepository instituteRepository,
                        CourseRepository courseRepository,
                        CategoryRepository categoryRepository,
                        AddressRepository addressRepository) {
                this.studentRepository = studentRepository;
                this.studentVerificationRepository = studentVerificationRepository;
                this.userRepository = userRepository;
                this.instituteRepository = instituteRepository;
                this.courseRepository = courseRepository;
                this.categoryRepository = categoryRepository;
                this.addressRepository = addressRepository;
        }

        @Override
        @Transactional(readOnly = true)
        public List<StudentSummaryResponse> getAllStudents() {

                List<Student> students = studentRepository.findAllBy();

                if (students.isEmpty()) {
                        throw new ResourceNotFoundException(
                                        "No students found");
                }

                return students.stream()
                                .map(this::mapToSummaryResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public List<StudentSummaryResponse> getStudentsForInstitute(UUID instituteUserId) {
                Institute institute = instituteRepository.findByUserId(instituteUserId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Institute profile not found for the authenticated user"));

                return studentRepository.findAllByInstituteId(institute.getId()).stream()
                                .map(this::mapToSummaryResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public StudentDetailedResponse getStudentById(
                        UUID studentId) {
                Student student = studentRepository
                                .findOneById(studentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Student not found with ID: "
                                                                + studentId));

                VerificationStatus verificationStatus = studentVerificationRepository
                                .findByStudentId(studentId)
                                .map(StudentVerification::getStatus)
                                .orElse(VerificationStatus.PENDING);

                return mapToDetailedResponse(
                                student,
                                verificationStatus);
        }

        @Override
        @Transactional
        public void completeProfile(
                        UUID userId,
                        CompleteStudentProfileRequest request) {
                if (studentRepository.existsByUserId(userId)) {
                        throw new BusinessException(
                                        "Student profile has already been completed");
                }

                String aadharNumber = request.aadharNumber().trim();

                if (studentRepository.existsByAadharNumber(
                                aadharNumber)) {
                        throw new BusinessException(
                                        "A student profile already exists with this Aadhaar number");
                }

                User user = userRepository
                                .findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with ID: "
                                                                + userId));

                Institute institute = instituteRepository
                                .findById(request.instituteId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Institute not found with ID: "
                                                                + request.instituteId()));

                Course course = courseRepository
                                .findOneById(request.courseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Course not found with ID: "
                                                                + request.courseId()));

                if (!course.getInstitute()
                                .getId()
                                .equals(institute.getId())) {

                        throw new BusinessException(
                                        "Selected course does not belong to the selected institute");
                }

                Category category = categoryRepository
                                .findById(request.categoryId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Category not found with ID: "
                                                                + request.categoryId()));

                Address address = addressRepository
                                .findById(request.addressId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Address not found with ID: "
                                                                + request.addressId()));

                Student student = new Student();

                student.setUser(user);
                student.setInstitute(institute);
                student.setCourse(course);
                student.setCategory(category);
                student.setAddress(address);

                student.setLocation(
                                request.location() == null
                                                ? null
                                                : request.location().trim());

                student.setPincode(request.pincode());
                student.setAadharNumber(aadharNumber);
                student.setGender(request.gender());
                student.setDateOfBirth(request.dateOfBirth());

                student.setFatherName(
                                request.fatherName().trim());

                student.setMotherName(
                                request.motherName().trim());

                student.setFeesPaid(BigDecimal.ZERO);
                student.setFeesPending(BigDecimal.ZERO);

                student.setAnnualFamilyIncome(
                                request.annualFamilyIncome());

                Student savedStudent = studentRepository.save(student);

                StudentVerification verification = new StudentVerification();

                verification.setStudent(savedStudent);
                verification.setInstitute(institute);
                verification.setStatus(
                                VerificationStatus.PENDING);
                verification.setRemark(null);
                verification.setVerifiedBy(null);
                verification.setVerifiedAt(null);

                studentVerificationRepository.save(verification);
        }

        private StudentSummaryResponse mapToSummaryResponse(
                        Student student) {
                User user = student.getUser();

                String instituteName = student.getInstitute() != null
                                ? student.getInstitute().getName()
                                : null;

                String courseName = student.getCourse() != null
                                ? student.getCourse().getName()
                                : null;

                return new StudentSummaryResponse(
                                student.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getMobile(),
                                maskAadhar(student.getAadharNumber()),
                                courseName,
                                instituteName);
        }

        private StudentDetailedResponse mapToDetailedResponse(
                        Student student,
                        VerificationStatus verificationStatus) {
                User user = student.getUser();

                return new StudentDetailedResponse(
                                student.getId(),
                                user.getId(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                user.getMobile(),
                                student.getInstitute().getName(),
                                student.getCourse().getName(),
                                student.getCategory().getCode(),
                                mapAddress(student.getAddress()),
                                student.getLocation(),
                                student.getPincode(),
                                maskAadhar(student.getAadharNumber()),
                                student.getGender(),
                                student.getDateOfBirth(),
                                student.getFatherName(),
                                student.getMotherName(),
                                student.getFeesPaid(),
                                student.getFeesPending(),
                                student.getAnnualFamilyIncome(),
                                verificationStatus,
                                true,
                                student.getCreatedAt(),
                                student.getUpdatedAt());
        }

        private InstituteSummaryResponse mapInstitute(
                        Institute institute) {
                if (institute == null) {
                        return null;
                }

                return new InstituteSummaryResponse(
                                institute.getId(),
                                institute.getName(),
                                institute.getLocation(),
                                institute.getPincode());
        }

        private CourseSummaryResponse mapCourse(
                        Course course) {
                if (course == null) {
                        return null;
                }

                UUID professionId = course.getProfession() != null
                                ? course.getProfession().getId()
                                : null;

                String professionName = course.getProfession() != null
                                ? course.getProfession().getName()
                                : null;

                return new CourseSummaryResponse(
                                course.getId(),
                                course.getName(),
                                course.getDurationYears(),
                                course.getFees(),
                                professionId,
                                professionName);
        }

        private AddressResponse mapAddress(
                        Address address) {
                if (address == null) {
                        return null;
                }

                return new AddressResponse(
                                address.getId(),
                                address.getCountry(),
                                address.getState(),
                                address.getDistrict(),
                                address.getCity());
        }

        private String maskAadhar(
                        String aadharNumber) {
                if (aadharNumber == null
                                || aadharNumber.length() < 4) {
                        return null;
                }

                return "XXXXXXXX"
                                + aadharNumber.substring(
                                                aadharNumber.length() - 4);
        }
}
