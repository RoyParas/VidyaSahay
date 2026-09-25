package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.ApplicationActionRequest;
import com.vidyasahay.vidyasahay.dto.request.ApplyApplicationRequest;
import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentDocumentResponse;
import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.ApplicationDocument;
import com.vidyasahay.vidyasahay.entity.ApplicationHistory;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentDocument;
import com.vidyasahay.vidyasahay.entity.StudentVerification;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {

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

        validateUploadedDocuments(
                request.documents(),
                request.documentTypeIds()
        );

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

            application.setLoanScheme(loanScheme);
            application.setScholarshipScheme(null);
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

            application.setScholarshipScheme(
                    scholarshipScheme
            );
            application.setLoanScheme(null);
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

    private void validateUploadedDocuments(
            MultipartFile[] documents,
            UUID[] documentTypeIds
    ) {
        if (documents == null || documents.length == 0) {
            return;
        }

        if (documentTypeIds == null ||
                documentTypeIds.length == 0) {
            throw new RuntimeException(
                    "Document type IDs are required"
            );
        }

        if (documents.length != documentTypeIds.length) {
            throw new RuntimeException(
                    "Each uploaded document must have " +
                    "one corresponding documentTypeId"
            );
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
        StudentDocument existingDocument =
                studentDocumentRepository
                        .findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
                                student.getId(),
                                documentTypeId
                        )
                        .orElse(null);

        /*
         * If both the database record and physical file exist,
         * reuse the existing StudentDocument.
         */
        if (existingDocument != null &&
                fileStorageService.exists(
                        existingDocument.getFilePath()
                )) {
            return existingDocument;
        }

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

        /*
         * StudentDocument exists in the database,
         * but its physical file is missing.
         * Update the existing record.
         */
        if (existingDocument != null) {
            existingDocument.setDocumentType(
                    documentType
            );
            existingDocument.setFileName(
                    originalFileName
            );
            existingDocument.setFilePath(
                    storedFilePath
            );
            existingDocument.setVerificationStatus(
                    VerificationStatus.PENDING
            );
            existingDocument.setVerifiedBy(null);
            existingDocument.setVerifiedAt(null);

            return studentDocumentRepository.save(
                    existingDocument
            );
        }

        /*
         * No StudentDocument exists for this student
         * and document type, so create a new record.
         */
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

        return new ApplicationDetailResponse(
                summary,
                studentResponse,
                instituteName,
                documents
        );
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

                student.getCourse() == null
                        ? null
                        : student.getCourse().getName(),

                student.getCategory() == null
                        ? null
                        : student.getCategory().getCode(),

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

                verification != null,
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
                "/api/application/documents/"
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