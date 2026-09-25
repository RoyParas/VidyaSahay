package com.vidyasahay.vidyasahay.support;

import com.vidyasahay.vidyasahay.entity.Address;
import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.Bank;
import com.vidyasahay.vidyasahay.entity.Category;
import com.vidyasahay.vidyasahay.entity.Course;
import com.vidyasahay.vidyasahay.entity.Disbursement;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.Institute;
import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentDocument;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.DisbursementStatus;
import com.vidyasahay.vidyasahay.enums.DisbursementType;
import com.vidyasahay.vidyasahay.enums.Gender;
import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Central factory for the entity graphs used across the unit tests.
 * <p>
 * The service implementations dereference a lot of nested associations while
 * mapping to DTOs, so the builders here deliberately return fully populated
 * objects. Every test can then mutate only the fields it actually cares about.
 */
public final class TestData {

    private TestData() {
    }

    // ------------------------------------------------------------ principals

    public static CustomUserPrincipal principal(UUID userId, RoleName role) {
        return new CustomUserPrincipal(
                userId,
                "Jane",
                "Doe",
                "jane@example.com",
                "hashed-password",
                role,
                true,
                false);
    }

    public static CustomUserPrincipal principal(RoleName role) {
        return principal(UUID.randomUUID(), role);
    }

    // ---------------------------------------------------------------- basics

    public static Role role(RoleName name) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(name);
        return role;
    }

    public static User user(RoleName roleName) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setRole(role(roleName));
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setMobile("9876543210");
        user.setHashedPassword("hashed-password");
        user.setActive(true);
        user.setMustChangePassword(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static Address address() {
        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setCountry("India");
        address.setState("Maharashtra");
        address.setDistrict("Mumbai Suburban");
        address.setCity("Mumbai");
        return address;
    }

    public static Category category() {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setCode("GEN");
        return category;
    }

    public static Profession profession() {
        Profession profession = new Profession();
        profession.setId(UUID.randomUUID());
        profession.setName("Engineering");
        return profession;
    }

    public static DocumentType documentType() {
        DocumentType documentType = new DocumentType();
        documentType.setId(UUID.randomUUID());
        documentType.setName("Aadhaar Card");
        documentType.setDescription("Government issued identity proof");
        return documentType;
    }

    // ------------------------------------------------------------ institutes

    public static Institute institute() {
        Institute institute = new Institute();
        institute.setId(UUID.randomUUID());
        institute.setUser(user(RoleName.INSTITUTE));
        institute.setName("Vidya Institute of Technology");
        institute.setAddress(address());
        institute.setLocation("Andheri");
        institute.setPincode(400053);
        institute.setBankName("State Bank");
        institute.setBranchName("Andheri West");
        institute.setIfscCode("SBIN0001234");
        institute.setAccountNumber("12345678901");
        return institute;
    }

    public static Course course(Institute institute) {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setInstitute(institute);
        course.setProfession(profession());
        course.setName("B.Tech Computer Engineering");
        course.setDurationYears(4);
        course.setFees(new BigDecimal("150000.00"));
        return course;
    }

    // --------------------------------------------------------------- student

    public static Student student() {
        Institute institute = institute();

        Student student = new Student();
        student.setId(UUID.randomUUID());
        student.setUser(user(RoleName.STUDENT));
        student.setInstitute(institute);
        student.setCourse(course(institute));
        student.setCategory(category());
        student.setAddress(address());
        student.setLocation("Andheri");
        student.setPincode(400053);
        student.setAadharNumber("123456789012");
        student.setGender(Gender.FEMALE);
        student.setDateOfBirth(LocalDate.of(2003, 5, 17));
        student.setFatherName("John Doe");
        student.setMotherName("Mary Doe");
        student.setFeesPaid(new BigDecimal("50000.00"));
        student.setFeesPending(new BigDecimal("100000.00"));
        student.setAnnualFamilyIncome(new BigDecimal("300000.00"));
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        return student;
    }

    public static StudentVerification verification(Student student, VerificationStatus status) {
        StudentVerification verification = new StudentVerification();
        verification.setId(UUID.randomUUID());
        verification.setStudent(student);
        verification.setInstitute(student.getInstitute());
        verification.setStatus(status);
        verification.setRemark("Looks fine");
        verification.setVerifiedAt(LocalDateTime.now());
        return verification;
    }

    public static StudentDocument studentDocument(Student student, VerificationStatus status) {
        StudentDocument document = new StudentDocument();
        document.setId(UUID.randomUUID());
        document.setStudent(student);
        document.setDocumentType(documentType());
        document.setFileName("aadhaar.pdf");
        document.setFilePath("aadhaar.pdf");
        document.setVerificationStatus(status);
        document.setVerifiedBy(null);
        document.setVerifiedAt(null);
        return document;
    }

    // ----------------------------------------------------------------- banks

    public static Bank bank() {
        Bank bank = new Bank();
        bank.setId(UUID.randomUUID());
        bank.setUser(user(RoleName.BANK));
        bank.setName("State Bank of Vidya");
        return bank;
    }

    // --------------------------------------------------------------- schemes

    public static LoanScheme loanScheme(User createdBy) {
        LoanScheme scheme = new LoanScheme();
        scheme.setId(UUID.randomUUID());
        scheme.setBank(bank());
        scheme.setName("Vidya Education Loan");
        scheme.setStatus(SchemeStatus.ACTIVE);
        scheme.setEffectiveFrom(LocalDate.now().minusDays(30));
        scheme.setEffectiveTo(LocalDate.now().plusDays(300));
        scheme.setMinLoanAmount(new BigDecimal("50000"));
        scheme.setMaxLoanAmount(new BigDecimal("1000000"));
        scheme.setInterestType(InterestType.FLOATING);
        scheme.setMinimumRate(new BigDecimal("8.50"));
        scheme.setMaximumRate(new BigDecimal("12.50"));
        scheme.setDisbursementType(DisbursementType.YEARLY);
        scheme.setCreatedBy(createdBy);
        scheme.setUpdatedBy(createdBy);
        scheme.setCreatedAt(LocalDateTime.now());
        scheme.setUpdatedAt(LocalDateTime.now());
        return scheme;
    }

    public static ScholarshipScheme scholarshipScheme(User createdBy) {
        ScholarshipScheme scheme = new ScholarshipScheme();
        scheme.setId(UUID.randomUUID());
        scheme.setName("National Merit Scholarship");
        scheme.setScholarshipType(ScholarshipType.MERIT_BASED);
        scheme.setAcademicYear("2025-26");
        scheme.setStartDate(LocalDate.now().minusDays(10));
        scheme.setEndDate(LocalDate.now().plusDays(100));
        scheme.setStatus(SchemeStatus.ACTIVE);
        scheme.setCreatedBy(createdBy);
        scheme.setUpdatedBy(createdBy);
        scheme.setCreatedAt(LocalDateTime.now());
        scheme.setUpdatedAt(LocalDateTime.now());
        return scheme;
    }

    // ---------------------------------------------------------- applications

    public static Application application(Student student, ApplicationType type) {
        Application application = new Application();
        application.setId(UUID.randomUUID());
        application.setStudent(student);
        application.setApplicationType(type);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        application.setApprovedAmount(new BigDecimal("250000.00"));

        if (type == ApplicationType.LOAN) {
            application.setLoanScheme(loanScheme(student.getUser()));
        } else {
            application.setScholarshipScheme(scholarshipScheme(student.getUser()));
        }

        return application;
    }

    public static Disbursement disbursement(Application application, User disbursedBy) {
        Disbursement disbursement = new Disbursement();
        disbursement.setId(UUID.randomUUID());
        disbursement.setApplication(application);
        disbursement.setApplicationType(application.getApplicationType());
        disbursement.setStudent(application.getStudent());
        disbursement.setDisbursedByUser(disbursedBy);
        disbursement.setAmount(new BigDecimal("125000.00"));
        disbursement.setDisbursementDate(LocalDate.now());
        disbursement.setStatus(DisbursementStatus.COMPLETED);
        disbursement.setRemark("First tranche");
        return disbursement;
    }

    public static PaymentFrequency defaultPaymentFrequency() {
        return PaymentFrequency.YEARLY;
    }
}
