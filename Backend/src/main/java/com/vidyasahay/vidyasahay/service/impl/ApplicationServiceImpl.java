package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.ApplicationActionRequest;
import com.vidyasahay.vidyasahay.dto.request.ApplyApplicationRequest;
import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSubmissionDetails;
import com.vidyasahay.vidyasahay.dto.response.ApplicationHistoryResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;
import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.ApplicationDocument;
import com.vidyasahay.vidyasahay.entity.ApplicationHistory;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.LoanSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.LoanSchemeRepaymentRule;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentDocument;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;
import com.vidyasahay.vidyasahay.enums.VerificationStatus;
import com.vidyasahay.vidyasahay.repository.ApplicationDocumentRepository;
import com.vidyasahay.vidyasahay.repository.ApplicationHistoryRepository;
import com.vidyasahay.vidyasahay.repository.ApplicationRepository;
import com.vidyasahay.vidyasahay.repository.DocumentTypeRepository;
import com.vidyasahay.vidyasahay.repository.StudentDocumentRepository;
import com.vidyasahay.vidyasahay.repository.StudentRepository;
import com.vidyasahay.vidyasahay.repository.StudentVerificationRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeRepository;
import com.vidyasahay.vidyasahay.service.ApplicationService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.FileStorageService;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final long MAX_APPLICATION_DOCUMENT_SIZE = 5L * 1024 * 1024;

    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;
    private final ApplicationHistoryRepository applicationHistoryRepository;
    private final StudentRepository studentRepository;
    private final StudentDocumentRepository studentDocumentRepository;
    private final StudentVerificationRepository studentVerificationRepository;
    private final LoanSchemeRepository loanSchemeRepository;
    private final ScholarshipSchemeRepository scholarshipSchemeRepository;
    private final UserRepository userRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final FileStorageService fileStorageService;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            ApplicationDocumentRepository applicationDocumentRepository,
            ApplicationHistoryRepository applicationHistoryRepository,
            StudentRepository studentRepository,
            StudentDocumentRepository studentDocumentRepository,
            StudentVerificationRepository studentVerificationRepository,
            LoanSchemeRepository loanSchemeRepository,
            ScholarshipSchemeRepository scholarshipSchemeRepository,
            UserRepository userRepository,
            DocumentTypeRepository documentTypeRepository,
            FileStorageService fileStorageService
    ) {
        this.applicationRepository = applicationRepository;
        this.applicationDocumentRepository = applicationDocumentRepository;
        this.applicationHistoryRepository = applicationHistoryRepository;
        this.studentRepository = studentRepository;
        this.studentDocumentRepository = studentDocumentRepository;
        this.studentVerificationRepository = studentVerificationRepository;
        this.loanSchemeRepository = loanSchemeRepository;
        this.scholarshipSchemeRepository = scholarshipSchemeRepository;
        this.userRepository = userRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationById(
            UUID applicationId
    ) {
        Application application = applicationRepository
                .findApplicationById(applicationId)
                .orElseThrow(() ->
                        new RuntimeException("Application not found")
                );

        return createApplicationDetailResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationById(UUID applicationId, CustomUserPrincipal principal) {
        Application application = applicationRepository.findApplicationById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (principal.getRole() == com.vidyasahay.vidyasahay.enums.RoleName.STUDENT
                && !application.getStudent().getUser().getId().equals(principal.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to view this application");
        }
        return createApplicationDetailResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationSummaryResponse> getMyApplications(
            CustomUserPrincipal principal
    ) {
        UUID userId = principal.getUserId();

        List<Application> applications =
                switch (principal.getRole()) {

                    case STUDENT ->
                            applicationRepository
                                    .findAllByStudentUserIdOrderBySubmittedAtDesc(
                                            userId
                                    );

                    case BANK ->
                            applicationRepository
                                    .findAllByLoanSchemeCreatedByIdOrderBySubmittedAtDesc(
                                            userId
                                    );

                    case GOVERNMENT ->
                            applicationRepository
                                    .findAllByScholarshipSchemeCreatedByIdOrderBySubmittedAtDesc(
                                            userId
                                    );

                    case INSTITUTE ->
                            applicationRepository
                                    .findAllByStudentInstituteUserIdOrderBySubmittedAtDesc(
                                            userId
                                    );

                    case ADMIN ->
                            applicationRepository
                                    .findAllByOrderBySubmittedAtDesc();
                };

        return applications.stream()
                .map(this::createApplicationSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationDetailResponse apply(
            ApplyApplicationRequest request,
            CustomUserPrincipal principal
    ) {
        Student student = studentRepository
                .findByUserId(principal.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student profile not found"
                        )
                );

        VerificationStatus studentVerificationStatus = studentVerificationRepository
                .findByStudentId(student.getId())
                .map(StudentVerification::getStatus)
                .orElse(VerificationStatus.PENDING);
        if (studentVerificationStatus != VerificationStatus.VERIFIED) {
            throw new BusinessException("Your institute must verify your profile before you can apply");
        }

        if (request.applicationType() == null ||
                request.applicationType().isBlank()) {
            throw new RuntimeException(
                    "Application type is required"
            );
        }

        if (request.schemeId() == null) {
            throw new RuntimeException(
                    "Scheme ID is required"
            );
        }

        ApplicationType applicationType;

        try {
            applicationType = ApplicationType.valueOf(
                    request.applicationType()
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(
                    "Invalid application type. " +
                    "Allowed values are LOAN and SCHOLARSHIP"
            );
        }

        Application application = new Application();

        application.setStudent(student);
        application.setApplicationType(applicationType);
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        application.setApprovedAmount(null);

        if (applicationType == ApplicationType.LOAN) {
            LoanScheme loanScheme = loanSchemeRepository
                    .findById(request.schemeId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Loan scheme not found"
                            )
                    );

            validateLoanApplication(student, loanScheme, request);
            validateUploadedDocuments(request.documents(), request.documentTypeIds(),
                    loanSchemeRepository.findRequiredDocumentTypeIds(loanScheme.getId()));

            application.setLoanScheme(loanScheme);
            application.setScholarshipScheme(null);
            application.setRequestedLoanAmount(request.requestedLoanAmount());
            application.setLoanPurpose(request.loanPurpose().trim());
            application.setRepaymentTenureYears(request.repaymentTenureYears());
            application.setCoBorrowerName(trimToNull(request.coBorrowerName()));
            application.setCoBorrowerIncome(request.coBorrowerIncome());
        }

        if (applicationType == ApplicationType.SCHOLARSHIP) {
            ScholarshipScheme scholarshipScheme =
                    scholarshipSchemeRepository
                            .findById(request.schemeId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Scholarship scheme not found"
                                    )
                    );

            validateScholarshipApplication(student, scholarshipScheme, request);
            validateUploadedDocuments(request.documents(), request.documentTypeIds(),
                    scholarshipSchemeRepository.findRequiredDocumentTypeIds(scholarshipScheme.getId()));

            application.setScholarshipScheme(
                    scholarshipScheme
            );
            application.setLoanScheme(null);
            application.setAcademicPercentage(request.academicPercentage());
        }

        Application savedApplication =
                applicationRepository.save(application);

        saveOrReuseApplicationDocuments(
                savedApplication,
                student,
                request.documents(),
                request.documentTypeIds()
        );

        createApplicationHistory(
                savedApplication,
                principal,
                ApplicationStatus.SUBMITTED,
                "Application submitted"
        );

        return createApplicationDetailResponse(
                savedApplication
        );
    }

    @Override
    @Transactional
    public ApplicationDetailResponse updateStatus(
            ApplicationActionRequest request,
            CustomUserPrincipal principal
    ) {
        Application application = applicationRepository
                .findApplicationById(
                        request.applicationId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found"
                        )
                );

        if (request.status() == null ||
                request.status().isBlank()) {
            throw new RuntimeException(
                    "Application status is required"
            );
        }

        ApplicationStatus status;

        try {
            status = ApplicationStatus.valueOf(
                    request.status()
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(
                    "Invalid application status: "
                            + request.status()
            );
        }

        if (status != ApplicationStatus.APPROVED
                && status != ApplicationStatus.REJECTED
                && status != ApplicationStatus.REVERTED) {
            throw new BusinessException("Application decision must be APPROVED, REJECTED, or REVERTED");
        }
        if (application.getStatus() != ApplicationStatus.SUBMITTED
                && application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new BusinessException("Only submitted or under-review applications can receive a decision");
        }
        if (request.remark() == null || request.remark().isBlank() || request.remark().trim().length() > 1000) {
            throw new BusinessException("A decision remark is required and must not exceed 1000 characters");
        }

        application.setStatus(status);

        if (request.approvedAmount() != null) {
            application.setApprovedAmount(
                    BigDecimal.valueOf(
                            request.approvedAmount()
                    )
            );
        }

        Application savedApplication =
                applicationRepository.save(application);

        createApplicationHistory(
                savedApplication,
                principal,
                status,
                request.remark()
        );

        return createApplicationDetailResponse(
                savedApplication
        );
    }

    @Override
    @Transactional
    public ApplicationDetailResponse resubmit(UUID applicationId, ApplyApplicationRequest request,
            CustomUserPrincipal principal) {
        Application application = applicationRepository.findApplicationById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (!application.getStudent().getUser().getId().equals(principal.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to resubmit this application");
        }
        if (application.getStatus() != ApplicationStatus.REVERTED) {
            throw new BusinessException("Only reverted applications can be resubmitted");
        }
        if (request.applicationType() == null
                || !application.getApplicationType().name().equalsIgnoreCase(request.applicationType())
                || request.schemeId() == null
                || !request.schemeId().equals(application.getApplicationType() == ApplicationType.LOAN
                        ? application.getLoanScheme().getId()
                        : application.getScholarshipScheme().getId())) {
            throw new BusinessException("The resubmitted application must use its original scheme and type");
        }

        Student student = application.getStudent();
        VerificationStatus verificationStatus = studentVerificationRepository.findByStudentId(student.getId())
                .map(StudentVerification::getStatus).orElse(VerificationStatus.PENDING);
        if (verificationStatus != VerificationStatus.VERIFIED) {
            throw new BusinessException("Your institute must verify your profile before you can resubmit");
        }

        List<UUID> requiredDocumentTypeIds;
        if (application.getApplicationType() == ApplicationType.LOAN) {
            LoanScheme scheme = application.getLoanScheme();
            validateLoanApplication(student, scheme, request);
            requiredDocumentTypeIds = loanSchemeRepository.findRequiredDocumentTypeIds(scheme.getId());
        } else {
            ScholarshipScheme scheme = application.getScholarshipScheme();
            validateScholarshipApplication(student, scheme, request);
            requiredDocumentTypeIds = scholarshipSchemeRepository.findRequiredDocumentTypeIds(scheme.getId());
        }
        validateUploadedDocuments(request.documents(), request.documentTypeIds(), requiredDocumentTypeIds);

        applicationDocumentRepository.deleteAllByApplicationId(applicationId);
        if (application.getApplicationType() == ApplicationType.LOAN) {
            application.setRequestedLoanAmount(request.requestedLoanAmount());
            application.setLoanPurpose(request.loanPurpose().trim());
            application.setRepaymentTenureYears(request.repaymentTenureYears());
            application.setCoBorrowerName(trimToNull(request.coBorrowerName()));
            application.setCoBorrowerIncome(request.coBorrowerIncome());
        } else {
            application.setAcademicPercentage(request.academicPercentage());
        }
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        Application savedApplication = applicationRepository.save(application);
        saveOrReuseApplicationDocuments(savedApplication, student, request.documents(), request.documentTypeIds());
        createApplicationHistory(savedApplication, principal, ApplicationStatus.SUBMITTED,
                "Application resubmitted with requested documents");
        return createApplicationDetailResponse(savedApplication);
    }

    private void validateLoanApplication(Student student, LoanScheme scheme, ApplyApplicationRequest request) {
        ensureSchemeCurrentlyActive(scheme.getStatus(), scheme.getEffectiveFrom(), scheme.getEffectiveTo());
        if (request.requestedLoanAmount() == null || request.requestedLoanAmount().signum() <= 0
                || (scheme.getMinLoanAmount() != null && request.requestedLoanAmount().compareTo(scheme.getMinLoanAmount()) < 0)
                || request.requestedLoanAmount().compareTo(scheme.getMaxLoanAmount()) > 0) {
            throw new BusinessException("Requested loan amount is outside this scheme's allowed range");
        }
        if (request.loanPurpose() == null || request.loanPurpose().isBlank() || request.loanPurpose().length() > 500) {
            throw new BusinessException("Loan purpose is required and must not exceed 500 characters");
        }
        LoanSchemeRepaymentRule repayment = loanSchemeRepository.findRepaymentRuleBySchemeId(scheme.getId())
                .orElseThrow(() -> new BusinessException("Loan repayment rules are not configured"));
        if (request.repaymentTenureYears() == null
                || request.repaymentTenureYears() < repayment.getMinTenureYears()
                || request.repaymentTenureYears() > repayment.getMaxTenureYears()) {
            throw new BusinessException("Repayment tenure is outside this scheme's allowed range");
        }
        LoanSchemeEligibility eligibility = loanSchemeRepository.findEligibilityBySchemeId(scheme.getId())
                .orElseThrow(() -> new BusinessException("Loan eligibility rules are not configured"));
        validateStudentAge(student, eligibility.getMinAge(), eligibility.getMaxAge());
        if (student.getCourse() == null || student.getCourse().getProfession() == null
                || !loanSchemeRepository.findEligibleProfessionIds(scheme.getId())
                        .contains(student.getCourse().getProfession().getId())) {
            throw new BusinessException("Student's course is not eligible for this loan scheme");
        }
        if (eligibility.isCoBorrowerRequired()) {
            if (request.coBorrowerName() == null || request.coBorrowerName().isBlank()
                    || request.coBorrowerName().trim().length() > 150
                    || request.coBorrowerIncome() == null || request.coBorrowerIncome().signum() < 0) {
                throw new BusinessException("A co-borrower name and non-negative annual income are required");
            }
        } else if (request.coBorrowerIncome() != null && request.coBorrowerIncome().signum() < 0) {
            throw new BusinessException("Co-borrower income cannot be negative");
        }
    }

    private void validateScholarshipApplication(Student student, ScholarshipScheme scheme, ApplyApplicationRequest request) {
        ensureSchemeCurrentlyActive(scheme.getStatus(), scheme.getStartDate(), scheme.getEndDate());
        if (request.academicPercentage() == null || request.academicPercentage().signum() < 0
                || request.academicPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("Academic percentage must be between 0 and 100");
        }
        ScholarshipSchemeEligibility eligibility = scholarshipSchemeRepository.findEligibilityBySchemeId(scheme.getId())
                .orElseThrow(() -> new BusinessException("Scholarship eligibility rules are not configured"));
        validateStudentAge(student, eligibility.getMinimumAge(), eligibility.getMaximumAge());
        if (student.getAnnualFamilyIncome() == null
                || (eligibility.getMaximumAnnualFamilyIncome() != null
                    && student.getAnnualFamilyIncome().compareTo(eligibility.getMaximumAnnualFamilyIncome()) > 0)
                || (eligibility.getMinimumPercentageCriteria() != null
                    && request.academicPercentage().compareTo(eligibility.getMinimumPercentageCriteria()) < 0)) {
            throw new BusinessException("Student does not meet this scholarship's financial or academic criteria");
        }
        if (student.getCourse() == null || student.getCourse().getProfession() == null
                || !scholarshipSchemeRepository.findEligibleProfessionIds(scheme.getId())
                        .contains(student.getCourse().getProfession().getId())) {
            throw new BusinessException("Student's course is not eligible for this scholarship");
        }
        if (scheme.getScholarshipType() == ScholarshipType.CATEGORY_BASED
                && (student.getCategory() == null
                    || !scholarshipSchemeRepository.findEligibleCategoryIds(scheme.getId())
                            .contains(student.getCategory().getId()))) {
            throw new BusinessException("Student's category is not eligible for this scholarship");
        }
    }

    private void validateStudentAge(Student student, Integer minimumAge, Integer maximumAge) {
        if (student.getDateOfBirth() == null) {
            throw new BusinessException("Student date of birth is required to check eligibility");
        }
        int age = Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
        if ((minimumAge != null && age < minimumAge) || (maximumAge != null && age > maximumAge)) {
            throw new BusinessException("Student age is outside this scheme's allowed range");
        }
    }

    private void ensureSchemeCurrentlyActive(SchemeStatus status, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (status != SchemeStatus.ACTIVE || startDate == null || endDate == null
                || startDate.isAfter(today) || endDate.isBefore(today)) {
            throw new BusinessException("This scheme is not currently accepting applications");
        }
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void validateUploadedDocuments(
            MultipartFile[] documents,
            UUID[] documentTypeIds,
            List<UUID> requiredDocumentTypeIds
    ) {
        if (requiredDocumentTypeIds == null) {
            throw new BusinessException("Required documents are not configured for this scheme");
        }
        if (requiredDocumentTypeIds.isEmpty() && (documents == null || documents.length == 0)
                && (documentTypeIds == null || documentTypeIds.length == 0)) {
            return;
        }

        if (documents == null || documentTypeIds == null || documents.length != requiredDocumentTypeIds.size()
                || documentTypeIds.length != documents.length) {
            throw new BusinessException("Upload exactly one document for every document required by this scheme");
        }
        Set<UUID> submittedDocumentTypes = new HashSet<>(Arrays.asList(documentTypeIds));
        if (submittedDocumentTypes.size() != documentTypeIds.length
                || !submittedDocumentTypes.equals(new HashSet<>(requiredDocumentTypeIds))) {
            throw new BusinessException("Upload exactly one document for every document required by this scheme");
        }
        for (MultipartFile file : documents) {
            validateDocumentFile(file);
        }
    }

    private void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Each required document must contain a file");
        }
        if (file.getSize() > MAX_APPLICATION_DOCUMENT_SIZE) {
            throw new BusinessException("Each uploaded document must be 5 MB or smaller");
        }
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        String extension = fileName == null || !fileName.contains(".")
                ? "" : fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        boolean signatureMatches = false;
        try {
            byte[] bytes = file.getBytes();
            if ("application/pdf".equalsIgnoreCase(contentType) && "pdf".equals(extension)) {
                signatureMatches = bytes.length >= 4 && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
            } else if ("image/jpeg".equalsIgnoreCase(contentType) && ("jpg".equals(extension) || "jpeg".equals(extension))) {
                signatureMatches = bytes.length >= 3 && (bytes[0] & 0xff) == 0xff && (bytes[1] & 0xff) == 0xd8 && (bytes[2] & 0xff) == 0xff;
            } else if ("image/png".equalsIgnoreCase(contentType) && "png".equals(extension)) {
                byte[] pngSignature = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
                signatureMatches = bytes.length >= pngSignature.length;
                for (int index = 0; signatureMatches && index < pngSignature.length; index++) {
                    signatureMatches = bytes[index] == pngSignature[index];
                }
            }
        } catch (java.io.IOException exception) {
            throw new BusinessException("Could not read an uploaded document");
        }
        if (!signatureMatches) {
            throw new BusinessException("Allowed document types are PDF, JPG, JPEG, and PNG, with matching file content");
        }
    }

    private void saveOrReuseApplicationDocuments(
            Application application,
            Student student,
            MultipartFile[] documents,
            UUID[] documentTypeIds
    ) {
        if (documents == null || documents.length == 0) {
            return;
        }

        for (int index = 0;
             index < documents.length;
             index++) {

            MultipartFile file = documents[index];
            UUID documentTypeId = documentTypeIds[index];

            if (file == null || file.isEmpty()) {
                throw new RuntimeException(
                        "Uploaded document at index "
                                + index
                                + " is empty"
                );
            }

            if (documentTypeId == null) {
                throw new RuntimeException(
                        "Document type ID at index "
                                + index
                                + " is required"
                );
            }

            StudentDocument studentDocument =
                    getExistingOrCreateStudentDocument(
                            student,
                            documentTypeId,
                            file
                    );

            createApplicationDocumentMapping(
                    application,
                    studentDocument
            );
        }
    }

    private StudentDocument getExistingOrCreateStudentDocument(
            Student student,
            UUID documentTypeId,
            MultipartFile file
    ) {
        DocumentType documentType =
                documentTypeRepository
                        .findById(documentTypeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document type not found: "
                                                + documentTypeId
                                )
                        );

        String storedFilePath =
                fileStorageService.store(file);

        String originalFileName =
                fileStorageService
                        .getCleanOriginalFileName(file);

        StudentDocument newStudentDocument =
                new StudentDocument();

        newStudentDocument.setStudent(student);
        newStudentDocument.setDocumentType(
                documentType
        );
        newStudentDocument.setFileName(
                originalFileName
        );
        newStudentDocument.setFilePath(
                storedFilePath
        );
        newStudentDocument.setVerificationStatus(
                VerificationStatus.PENDING
        );
        newStudentDocument.setVerifiedBy(null);
        newStudentDocument.setVerifiedAt(null);

        return studentDocumentRepository.save(
                newStudentDocument
        );
    }

    private void createApplicationDocumentMapping(
            Application application,
            StudentDocument studentDocument
    ) {
        boolean mappingExists =
                applicationDocumentRepository
                        .existsByApplication_IdAndStudentDocument_Id(
                                application.getId(),
                                studentDocument.getId()
                        );

        if (mappingExists) {
            return;
        }

        ApplicationDocument mapping =
                new ApplicationDocument();

        mapping.setApplication(application);
        mapping.setStudentDocument(studentDocument);

        applicationDocumentRepository.save(mapping);
    }

    private void createApplicationHistory(
            Application application,
            CustomUserPrincipal principal,
            ApplicationStatus status,
            String remark
    ) {
        User actionByUser = userRepository
                .findById(principal.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );

        ApplicationHistory history =
                new ApplicationHistory();

        history.setApplication(application);
        history.setStatus(status);
        history.setRemark(remark);
        history.setActionByUser(actionByUser);
        history.setAssignedToUser(null);
        history.setCreatedAt(LocalDateTime.now());

        applicationHistoryRepository.save(history);
    }

    private ApplicationDetailResponse createApplicationDetailResponse(
            Application application
    ) {
        ApplicationSummaryResponse summary =
                createApplicationSummaryResponse(
                        application
                );

        StudentDetailedResponse studentResponse =
                createStudentDetailedResponse(
                        application.getStudent()
                );

        List<StudentDocumentResponse> documents =
                applicationDocumentRepository
                        .findAllByApplicationId(
                                application.getId()
                        )
                        .stream()
                        .map(
                                ApplicationDocument
                                        ::getStudentDocument
                        )
                        .map(this::createStudentDocumentResponse)
                        .toList();

        String instituteName =
                application.getStudent()
                        .getInstitute() == null
                        ? null
                        : application.getStudent()
                                .getInstitute()
                                .getName();

        List<ApplicationHistoryResponse> history = applicationHistoryRepository
                .findAllByApplicationIdOrderByCreatedAtDesc(application.getId())
                .stream()
                .map(entry -> new ApplicationHistoryResponse(
                        entry.getId(),
                        entry.getStatus(),
                        entry.getRemark(),
                        entry.getActionByUser().getId(),
                        fullName(entry.getActionByUser()),
                        entry.getAssignedToUser() == null ? null : entry.getAssignedToUser().getId(),
                        entry.getAssignedToUser() == null ? null : fullName(entry.getAssignedToUser()),
                        entry.getCreatedAt()))
                .toList();

        return new ApplicationDetailResponse(
                summary,
                new ApplicationSubmissionDetails(
                        application.getRequestedLoanAmount(),
                        application.getAcademicPercentage(),
                        application.getLoanPurpose(),
                        application.getRepaymentTenureYears(),
                        application.getCoBorrowerName(),
                        application.getCoBorrowerIncome()),
                studentResponse,
                instituteName,
                documents,
                history
        );
    }

    private String fullName(User user) {
        return java.util.stream.Stream.of(user.getFirstName(), user.getLastName())
                .filter(name -> name != null && !name.isBlank())
                .reduce((first, last) -> first + " " + last)
                .orElse("");
    }

    private ApplicationSummaryResponse createApplicationSummaryResponse(
            Application application
    ) {
        UUID schemeId = null;
        String schemeName = null;

        if (application.getApplicationType() ==
                ApplicationType.LOAN &&
                application.getLoanScheme() != null) {

            schemeId = application
                    .getLoanScheme()
                    .getId();

            schemeName = application
                    .getLoanScheme()
                    .getName();
        }

        if (application.getApplicationType() ==
                ApplicationType.SCHOLARSHIP &&
                application.getScholarshipScheme() != null) {

            schemeId = application
                    .getScholarshipScheme()
                    .getId();

            schemeName = application
                    .getScholarshipScheme()
                    .getName();
        }

        return new ApplicationSummaryResponse(
                application.getId(),
                application.getApplicationType(),
                schemeId,
                schemeName,
                application.getStudent().getId(),
                application.getStudent()
                        .getUser()
                        .getFirstName(),
                application.getStudent()
                        .getUser()
                        .getLastName(),
                application.getStatus(),
                application.getApprovedAmount()
        );
    }

    private StudentDetailedResponse createStudentDetailedResponse(
            Student student
    ) {
        StudentVerification verification =
                studentVerificationRepository
                        .findByStudentId(student.getId())
                        .orElse(null);

        AddressResponse addressResponse = null;

        if (student.getAddress() != null) {
            addressResponse = new AddressResponse(
                    student.getAddress().getId(),
                    student.getAddress().getCountry(),
                    student.getAddress().getState(),
                    student.getAddress().getDistrict(),
                    student.getAddress().getCity()
            );
        }

        return new StudentDetailedResponse(
                student.getId(),
                student.getUser().getId(),
                student.getUser().getFirstName(),
                student.getUser().getLastName(),
                student.getUser().getEmail(),
                student.getUser().getMobile(),

                student.getInstitute() == null
                        ? null
                        : student.getInstitute().getName(),

                student.getInstitute() == null
                        ? null
                        : student.getInstitute().getId(),

                student.getCourse() == null
                        ? null
                        : student.getCourse().getName(),

                student.getCourse() == null
                        ? null
                        : student.getCourse().getId(),

                student.getCategory() == null
                        ? null
                        : student.getCategory().getCode(),

                student.getCategory() == null
                        ? null
                        : student.getCategory().getId(),

                addressResponse,
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

                verification == null
                        ? null
                        : verification.getStatus(),

                student.getUser().isProfileCompleted(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }

    private StudentDocumentResponse createStudentDocumentResponse(
            StudentDocument document
    ) {
        /*
         * Angular will access the physical file
         * through this backend URL.
         */
        String fileUrl =
                "/api/document/"
                        + document.getId()
                        + "/file";

        return new StudentDocumentResponse(
                document.getId(),
                document.getStudent().getId(),
                document.getDocumentType().getId(),
                document.getDocumentType().getName(),
                document.getDocumentType()
                        .getDescription(),
                document.getFileName(),
                fileUrl,

                document.getVerificationStatus() == null
                        ? null
                        : document.getVerificationStatus()
                                .name(),

                document.getVerifiedBy() == null
                        ? null
                        : document.getVerifiedBy()
                                .getId(),

                document.getVerifiedAt()
        );
    }

    private String maskAadhar(
            String aadharNumber
    ) {
        if (aadharNumber == null ||
                aadharNumber.length() < 4) {
            return null;
        }

        return "XXXXXXXX"
                + aadharNumber.substring(
                        aadharNumber.length() - 4
                );
    }
}
