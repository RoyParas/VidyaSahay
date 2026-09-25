package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.ApplicationActionRequest;
import com.vidyasahay.vidyasahay.dto.request.ApplyApplicationRequest;
import com.vidyasahay.vidyasahay.dto.response.ApplicationDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.ApplicationSummaryResponse;
import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.ApplicationDocument;
import com.vidyasahay.vidyasahay.entity.ApplicationHistory;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.StudentDocument;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.ApplicationStatus;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.RoleName;
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
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.FileStorageService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
@DisplayName("ApplicationServiceImpl")
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationDocumentRepository applicationDocumentRepository;

    @Mock
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentDocumentRepository studentDocumentRepository;

    @Mock
    private StudentVerificationRepository studentVerificationRepository;

    @Mock
    private LoanSchemeRepository loanSchemeRepository;

    @Mock
    private ScholarshipSchemeRepository scholarshipSchemeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = TestData.student();
    }

    @Nested
    @DisplayName("getApplicationById")
    class GetApplicationById {

        @Test
        @DisplayName("builds full detail response for loan application")
        void getApplicationById_loan() {
            Application application =
                    TestData.application(
                            student,
                            ApplicationType.LOAN
                    );

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            when(studentVerificationRepository.findByStudentId(
                    student.getId()
            )).thenReturn(
                    Optional.of(
                            TestData.verification(
                                    student,
                                    VerificationStatus.VERIFIED
                            )
                    )
            );

            when(applicationDocumentRepository.findAllByApplicationId(
                    application.getId()
            )).thenReturn(List.of());

            ApplicationDetailResponse response =
                    applicationService.getApplicationById(
                            application.getId()
                    );

            assertThat(response).isNotNull();

            assertThat(response.applicationSummary().id())
                    .isEqualTo(application.getId());

            assertThat(
                    response.applicationSummary()
                            .applicationType()
            ).isEqualTo(ApplicationType.LOAN);

            assertThat(response.applicationSummary().schemeId())
                    .isEqualTo(
                            application.getLoanScheme().getId()
                    );

            assertThat(response.applicationSummary().schemeName())
                    .isEqualTo(
                            application.getLoanScheme().getName()
                    );

            assertThat(response.student().studentId())
                    .isEqualTo(student.getId());

            assertThat(response.student().verificationStatus())
                    .isEqualTo(VerificationStatus.VERIFIED);

            assertThat(response.student().profileCompleted())
                    .isTrue();

            assertThat(response.instituteName())
                    .isEqualTo(
                            student.getInstitute().getName()
                    );

            assertThat(response.documents()).isEmpty();
        }

        @Test
        @DisplayName("builds detail response for scholarship application")
        void getApplicationById_scholarship() {
            Application application =
                    TestData.application(
                            student,
                            ApplicationType.SCHOLARSHIP
                    );

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            when(studentVerificationRepository.findByStudentId(
                    student.getId()
            )).thenReturn(Optional.empty());

            when(applicationDocumentRepository.findAllByApplicationId(
                    application.getId()
            )).thenReturn(List.of());

            ApplicationDetailResponse response =
                    applicationService.getApplicationById(
                            application.getId()
                    );

            assertThat(
                    response.applicationSummary()
                            .applicationType()
            ).isEqualTo(ApplicationType.SCHOLARSHIP);

            assertThat(response.applicationSummary().schemeId())
                    .isEqualTo(
                            application
                                    .getScholarshipScheme()
                                    .getId()
                    );

            assertThat(response.applicationSummary().schemeName())
                    .isEqualTo(
                            application
                                    .getScholarshipScheme()
                                    .getName()
                    );

            assertThat(response.student().verificationStatus())
                    .isNull();

            assertThat(response.student().profileCompleted())
                    .isFalse();
        }

        @Test
        @DisplayName("maps linked student documents")
        void getApplicationById_withDocuments() {
            Application application =
                    TestData.application(
                            student,
                            ApplicationType.LOAN
                    );

            StudentDocument studentDocument =
                    TestData.studentDocument(
                            student,
                            VerificationStatus.VERIFIED
                    );

            ApplicationDocument mapping =
                    new ApplicationDocument();

            mapping.setId(UUID.randomUUID());
            mapping.setApplication(application);
            mapping.setStudentDocument(studentDocument);

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            when(studentVerificationRepository.findByStudentId(
                    student.getId()
            )).thenReturn(Optional.empty());

            when(applicationDocumentRepository.findAllByApplicationId(
                    application.getId()
            )).thenReturn(List.of(mapping));

            ApplicationDetailResponse response =
                    applicationService.getApplicationById(
                            application.getId()
                    );

            assertThat(response.documents()).hasSize(1);

            assertThat(response.documents().get(0).id())
                    .isEqualTo(studentDocument.getId());

            assertThat(
                    response.documents()
                            .get(0)
                            .verificationStatus()
            ).isEqualTo("VERIFIED");

            /*
             * If your DTO property is fileUrl,
             * replace filePath() with fileUrl().
             */
            assertThat(
                    response.documents()
                            .get(0)
                            .fileUrl()
            ).isEqualTo(
                    "/api/application/documents/"
                            + studentDocument.getId()
                            + "/file"
            );
        }

        @Test
        @DisplayName("fails when application does not exist")
        void getApplicationById_notFound() {
            UUID applicationId = UUID.randomUUID();

            when(applicationRepository.findApplicationById(
                    applicationId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.getApplicationById(
                            applicationId
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Application not found"
                    );
        }
    }

    @Nested
    @DisplayName("getMyApplications")
    class GetMyApplications {

        @Test
        @DisplayName("uses student query for STUDENT")
        void getMyApplications_student() {
            CustomUserPrincipal principal =
                    TestData.principal(
                            student.getUser().getId(),
                            RoleName.STUDENT
                    );

            Application application =
                    TestData.application(
                            student,
                            ApplicationType.LOAN
                    );

            when(
                    applicationRepository
                            .findAllByStudentUserIdOrderBySubmittedAtDesc(
                                    principal.getUserId()
                            )
            ).thenReturn(List.of(application));

            List<ApplicationSummaryResponse> response =
                    applicationService.getMyApplications(
                            principal
                    );

            assertThat(response).hasSize(1);

            assertThat(response.get(0).id())
                    .isEqualTo(application.getId());

            assertThat(response.get(0).status())
                    .isEqualTo(
                            ApplicationStatus.SUBMITTED
                    );

            assertThat(response.get(0).studentFirstName())
                    .isEqualTo(
                            student.getUser().getFirstName()
                    );
        }

        @Test
        @DisplayName("uses loan-scheme query for BANK")
        void getMyApplications_bank() {
            CustomUserPrincipal principal =
                    TestData.principal(RoleName.BANK);

            when(
                    applicationRepository
                            .findAllByLoanSchemeCreatedByIdOrderBySubmittedAtDesc(
                                    principal.getUserId()
                            )
            ).thenReturn(List.of());

            assertThat(
                    applicationService.getMyApplications(
                            principal
                    )
            ).isEmpty();
        }

        @Test
        @DisplayName("uses scholarship-scheme query for GOVERNMENT")
        void getMyApplications_government() {
            CustomUserPrincipal principal =
                    TestData.principal(
                            RoleName.GOVERNMENT
                    );

            when(
                    applicationRepository
                            .findAllByScholarshipSchemeCreatedByIdOrderBySubmittedAtDesc(
                                    principal.getUserId()
                            )
            ).thenReturn(List.of());

            assertThat(
                    applicationService.getMyApplications(
                            principal
                    )
            ).isEmpty();
        }

        @Test
        @DisplayName("uses institute query for INSTITUTE")
        void getMyApplications_institute() {
            CustomUserPrincipal principal =
                    TestData.principal(
                            RoleName.INSTITUTE
                    );

            when(
                    applicationRepository
                            .findAllByStudentInstituteUserIdOrderBySubmittedAtDesc(
                                    principal.getUserId()
                            )
            ).thenReturn(List.of());

            assertThat(
                    applicationService.getMyApplications(
                            principal
                    )
            ).isEmpty();
        }

        @Test
        @DisplayName("returns all applications for ADMIN")
        void getMyApplications_admin() {
            CustomUserPrincipal principal =
                    TestData.principal(RoleName.ADMIN);

            Application application =
                    TestData.application(
                            student,
                            ApplicationType.SCHOLARSHIP
                    );

            when(
                    applicationRepository
                            .findAllByOrderBySubmittedAtDesc()
            ).thenReturn(List.of(application));

            assertThat(
                    applicationService.getMyApplications(
                            principal
                    )
            ).hasSize(1);
        }
    }

    @Nested
    @DisplayName("apply")
    class Apply {

        private CustomUserPrincipal principal;

        @BeforeEach
        void setUp() {
            principal = TestData.principal(
                    student.getUser().getId(),
                    RoleName.STUDENT
            );
        }

        private void stubApplicationSave(
                UUID applicationId
        ) {
            when(
                    applicationRepository.save(
                            any(Application.class)
                    )
            ).thenAnswer(invocation -> {
                Application saved =
                        invocation.getArgument(0);

                saved.setId(applicationId);

                return saved;
            });
        }

        private void stubDetailResponse(
                UUID applicationId
        ) {
            when(
                    studentVerificationRepository
                            .findByStudentId(
                                    student.getId()
                            )
            ).thenReturn(Optional.empty());

            when(
                    applicationDocumentRepository
                            .findAllByApplicationId(
                                    applicationId
                            )
            ).thenReturn(List.of());
        }

        @Test
        @DisplayName("creates SUBMITTED loan application and history")
        void apply_loan() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            UUID applicationId =
                    UUID.randomUUID();

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(
                    Optional.of(student.getUser())
            );

            stubDetailResponse(applicationId);

            ApplyApplicationRequest request =
                    new ApplyApplicationRequest(
                            "loan",
                            loanScheme.getId(),
                            null,
                            null
                    );

            ApplicationDetailResponse response =
                    applicationService.apply(
                            request,
                            principal
                    );

            assertThat(response.applicationSummary().id())
                    .isEqualTo(applicationId);

            assertThat(
                    response.applicationSummary()
                            .applicationType()
            ).isEqualTo(ApplicationType.LOAN);

            assertThat(response.applicationSummary().status())
                    .isEqualTo(
                            ApplicationStatus.SUBMITTED
                    );

            assertThat(
                    response.applicationSummary()
                            .approvedAmount()
            ).isNull();

            ArgumentCaptor<Application> applicationCaptor =
                    ArgumentCaptor.forClass(
                            Application.class
                    );

            verify(applicationRepository)
                    .save(applicationCaptor.capture());

            Application capturedApplication =
                    applicationCaptor.getValue();

            assertThat(capturedApplication.getLoanScheme())
                    .isSameAs(loanScheme);

            assertThat(
                    capturedApplication
                            .getScholarshipScheme()
            ).isNull();

            assertThat(capturedApplication.getSubmittedAt())
                    .isNotNull();

            ArgumentCaptor<ApplicationHistory> historyCaptor =
                    ArgumentCaptor.forClass(
                            ApplicationHistory.class
                    );

            verify(applicationHistoryRepository)
                    .save(historyCaptor.capture());

            ApplicationHistory capturedHistory =
                    historyCaptor.getValue();

            assertThat(capturedHistory.getStatus())
                    .isEqualTo(
                            ApplicationStatus.SUBMITTED
                    );

            assertThat(capturedHistory.getRemark())
                    .isEqualTo(
                            "Application submitted"
                    );

            assertThat(capturedHistory.getActionByUser())
                    .isSameAs(student.getUser());
        }

        @Test
        @DisplayName("creates SUBMITTED scholarship application")
        void apply_scholarship() {
            ScholarshipScheme scheme =
                    TestData.scholarshipScheme(
                            student.getUser()
                    );

            UUID applicationId =
                    UUID.randomUUID();

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(
                    scholarshipSchemeRepository
                            .findById(scheme.getId())
            ).thenReturn(Optional.of(scheme));

            stubApplicationSave(applicationId);

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(
                    Optional.of(student.getUser())
            );

            stubDetailResponse(applicationId);

            ApplyApplicationRequest request =
                    new ApplyApplicationRequest(
                            "SCHOLARSHIP",
                            scheme.getId(),
                            null,
                            null
                    );

            ApplicationDetailResponse response =
                    applicationService.apply(
                            request,
                            principal
                    );

            assertThat(
                    response.applicationSummary()
                            .applicationType()
            ).isEqualTo(
                    ApplicationType.SCHOLARSHIP
            );

            assertThat(
                    response.applicationSummary()
                            .schemeId()
            ).isEqualTo(scheme.getId());
        }

        @Test
        @DisplayName("reuses existing student document")
        void apply_reusesExistingStudentDocument() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            StudentDocument existingDocument =
                    TestData.studentDocument(
                            student,
                            VerificationStatus.VERIFIED
                    );

            UUID applicationId =
                    UUID.randomUUID();

            UUID documentTypeId =
                    existingDocument
                            .getDocumentType()
                            .getId();

            MultipartFile uploadedFile =
                    createFile(
                            "aadhar.pdf",
                            "application/pdf"
                    );

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            when(
                    studentDocumentRepository
                            .findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
                                    student.getId(),
                                    documentTypeId
                            )
            ).thenReturn(
                    Optional.of(existingDocument)
            );

            when(
                    fileStorageService.exists(
                            existingDocument.getFilePath()
                    )
            ).thenReturn(true);

            when(
                    applicationDocumentRepository
                            .existsByApplication_IdAndStudentDocument_Id(
                                    applicationId,
                                    existingDocument.getId()
                            )
            ).thenReturn(false);

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(
                    Optional.of(student.getUser())
            );

            stubDetailResponse(applicationId);

            ApplyApplicationRequest request =
                    new ApplyApplicationRequest(
                            "LOAN",
                            loanScheme.getId(),
                            new MultipartFile[]{
                                    uploadedFile
                            },
                            new UUID[]{
                                    documentTypeId
                            }
                    );

            applicationService.apply(
                    request,
                    principal
            );

            verify(
                    fileStorageService,
                    never()
            ).store(any(MultipartFile.class));

            verify(
                    studentDocumentRepository,
                    never()
            ).save(any(StudentDocument.class));

            ArgumentCaptor<ApplicationDocument> mappingCaptor =
                    ArgumentCaptor.forClass(
                            ApplicationDocument.class
                    );

            verify(applicationDocumentRepository)
                    .save(mappingCaptor.capture());

            ApplicationDocument capturedMapping =
                    mappingCaptor.getValue();

            assertThat(
                    capturedMapping.getStudentDocument()
            ).isSameAs(existingDocument);

            assertThat(
                    capturedMapping.getApplication().getId()
            ).isEqualTo(applicationId);
        }

        @Test
        @DisplayName("stores new file and creates StudentDocument")
        void apply_createsNewStudentDocument() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            StudentDocument sampleDocument =
                    TestData.studentDocument(
                            student,
                            VerificationStatus.VERIFIED
                    );

            DocumentType documentType =
                    sampleDocument.getDocumentType();

            UUID documentTypeId =
                    documentType.getId();

            UUID applicationId =
                    UUID.randomUUID();

            UUID studentDocumentId =
                    UUID.randomUUID();

            MultipartFile uploadedFile =
                    createFile(
                            "aadhar.pdf",
                            "application/pdf"
                    );

            String storedPath =
                    "src/main/resources/uploads/documents/"
                            + UUID.randomUUID()
                            + "_aadhar.pdf";

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            when(
                    studentDocumentRepository
                            .findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
                                    student.getId(),
                                    documentTypeId
                            )
            ).thenReturn(Optional.empty());

            when(documentTypeRepository.findById(
                    documentTypeId
            )).thenReturn(Optional.of(documentType));

            when(fileStorageService.store(
                    uploadedFile
            )).thenReturn(storedPath);

            when(
                    fileStorageService
                            .getCleanOriginalFileName(
                                    uploadedFile
                            )
            ).thenReturn("aadhar.pdf");

            when(
                    studentDocumentRepository
                            .save(any(StudentDocument.class))
            ).thenAnswer(invocation -> {
                StudentDocument savedDocument =
                        invocation.getArgument(0);

                savedDocument.setId(
                        studentDocumentId
                );

                return savedDocument;
            });

            when(
                    applicationDocumentRepository
                            .existsByApplication_IdAndStudentDocument_Id(
                                    applicationId,
                                    studentDocumentId
                            )
            ).thenReturn(false);

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(
                    Optional.of(student.getUser())
            );

            stubDetailResponse(applicationId);

            ApplyApplicationRequest request =
                    new ApplyApplicationRequest(
                            "LOAN",
                            loanScheme.getId(),
                            new MultipartFile[]{
                                    uploadedFile
                            },
                            new UUID[]{
                                    documentTypeId
                            }
                    );

            applicationService.apply(
                    request,
                    principal
            );

            verify(fileStorageService)
                    .store(uploadedFile);

            ArgumentCaptor<StudentDocument> documentCaptor =
                    ArgumentCaptor.forClass(
                            StudentDocument.class
                    );

            verify(studentDocumentRepository)
                    .save(documentCaptor.capture());

            StudentDocument capturedDocument =
                    documentCaptor.getValue();

            assertThat(capturedDocument.getStudent())
                    .isSameAs(student);

            assertThat(capturedDocument.getDocumentType())
                    .isSameAs(documentType);

            assertThat(capturedDocument.getFileName())
                    .isEqualTo("aadhar.pdf");

            assertThat(capturedDocument.getFilePath())
                    .isEqualTo(storedPath);

            assertThat(
                    capturedDocument.getVerificationStatus()
            ).isEqualTo(VerificationStatus.PENDING);

            assertThat(capturedDocument.getVerifiedBy())
                    .isNull();

            assertThat(capturedDocument.getVerifiedAt())
                    .isNull();

            ArgumentCaptor<ApplicationDocument> mappingCaptor =
                    ArgumentCaptor.forClass(
                            ApplicationDocument.class
                    );

            verify(applicationDocumentRepository)
                    .save(mappingCaptor.capture());

            ApplicationDocument capturedMapping =
                    mappingCaptor.getValue();

            assertThat(
                    capturedMapping
                            .getStudentDocument()
                            .getId()
            ).isEqualTo(studentDocumentId);

            assertThat(
                    capturedMapping
                            .getApplication()
                            .getId()
            ).isEqualTo(applicationId);
        }

        @Test
        @DisplayName("updates existing document when physical file is missing")
        void apply_updatesExistingDocumentWhenFileMissing() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            StudentDocument existingDocument =
                    TestData.studentDocument(
                            student,
                            VerificationStatus.VERIFIED
                    );

            DocumentType documentType =
                    existingDocument.getDocumentType();

            UUID documentTypeId =
                    documentType.getId();

            UUID applicationId =
                    UUID.randomUUID();

            MultipartFile uploadedFile =
                    createFile(
                            "new-aadhar.pdf",
                            "application/pdf"
                    );

            String storedPath =
                    "src/main/resources/uploads/documents/"
                            + UUID.randomUUID()
                            + "_new-aadhar.pdf";

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            when(
                    studentDocumentRepository
                            .findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
                                    student.getId(),
                                    documentTypeId
                            )
            ).thenReturn(
                    Optional.of(existingDocument)
            );

            when(
                    fileStorageService.exists(
                            existingDocument.getFilePath()
                    )
            ).thenReturn(false);

            when(documentTypeRepository.findById(
                    documentTypeId
            )).thenReturn(Optional.of(documentType));

            when(fileStorageService.store(
                    uploadedFile
            )).thenReturn(storedPath);

            when(
                    fileStorageService
                            .getCleanOriginalFileName(
                                    uploadedFile
                            )
            ).thenReturn("new-aadhar.pdf");

            when(
                    studentDocumentRepository.save(
                            existingDocument
                    )
            ).thenReturn(existingDocument);

            when(
                    applicationDocumentRepository
                            .existsByApplication_IdAndStudentDocument_Id(
                                    applicationId,
                                    existingDocument.getId()
                            )
            ).thenReturn(false);

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(
                    Optional.of(student.getUser())
            );

            stubDetailResponse(applicationId);

            applicationService.apply(
                    new ApplyApplicationRequest(
                            "LOAN",
                            loanScheme.getId(),
                            new MultipartFile[]{
                                    uploadedFile
                            },
                            new UUID[]{
                                    documentTypeId
                            }
                    ),
                    principal
            );

            verify(studentDocumentRepository)
                    .save(existingDocument);

            assertThat(existingDocument.getFileName())
                    .isEqualTo("new-aadhar.pdf");

            assertThat(existingDocument.getFilePath())
                    .isEqualTo(storedPath);

            assertThat(
                    existingDocument.getVerificationStatus()
            ).isEqualTo(VerificationStatus.PENDING);

            assertThat(existingDocument.getVerifiedBy())
                    .isNull();

            assertThat(existingDocument.getVerifiedAt())
                    .isNull();

            verify(applicationDocumentRepository)
                    .save(any(ApplicationDocument.class));
        }

        @Test
        @DisplayName("does not create duplicate application-document mapping")
        void apply_mappingAlreadyExists() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            StudentDocument existingDocument =
                    TestData.studentDocument(
                            student,
                            VerificationStatus.VERIFIED
                    );

            UUID documentTypeId =
                    existingDocument
                            .getDocumentType()
                            .getId();

            UUID applicationId =
                    UUID.randomUUID();

            MultipartFile uploadedFile =
                    createFile(
                            "aadhar.pdf",
                            "application/pdf"
                    );

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            when(
                    studentDocumentRepository
                            .findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
                                    student.getId(),
                                    documentTypeId
                            )
            ).thenReturn(
                    Optional.of(existingDocument)
            );

            when(
                    fileStorageService.exists(
                            existingDocument.getFilePath()
                    )
            ).thenReturn(true);

            when(
                    applicationDocumentRepository
                            .existsByApplication_IdAndStudentDocument_Id(
                                    applicationId,
                                    existingDocument.getId()
                            )
            ).thenReturn(true);

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(
                    Optional.of(student.getUser())
            );

            stubDetailResponse(applicationId);

            applicationService.apply(
                    new ApplyApplicationRequest(
                            "LOAN",
                            loanScheme.getId(),
                            new MultipartFile[]{
                                    uploadedFile
                            },
                            new UUID[]{
                                    documentTypeId
                            }
                    ),
                    principal
            );

            verify(
                    applicationDocumentRepository,
                    never()
            ).save(any(ApplicationDocument.class));
        }

        @Test
        @DisplayName("rejects missing document type ids")
        void apply_missingDocumentTypeIds() {
            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            MultipartFile uploadedFile =
                    createFile(
                            "aadhar.pdf",
                            "application/pdf"
                    );

            ApplyApplicationRequest request =
                    new ApplyApplicationRequest(
                            "LOAN",
                            UUID.randomUUID(),
                            new MultipartFile[]{
                                    uploadedFile
                            },
                            null
                    );

            assertThatThrownBy(() ->
                    applicationService.apply(
                            request,
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Document type IDs are required"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }

        @Test
        @DisplayName("rejects different document and document type counts")
        void apply_documentCountMismatch() {
            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            MultipartFile firstFile =
                    createFile(
                            "aadhar.pdf",
                            "application/pdf"
                    );

            MultipartFile secondFile =
                    createFile(
                            "marksheet.pdf",
                            "application/pdf"
                    );

            ApplyApplicationRequest request =
                    new ApplyApplicationRequest(
                            "LOAN",
                            UUID.randomUUID(),
                            new MultipartFile[]{
                                    firstFile,
                                    secondFile
                            },
                            new UUID[]{
                                    UUID.randomUUID()
                            }
                    );

            assertThatThrownBy(() ->
                    applicationService.apply(
                            request,
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "one corresponding documentTypeId"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }

        @Test
        @DisplayName("rejects empty uploaded file")
        void apply_emptyUploadedFile() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            UUID applicationId =
                    UUID.randomUUID();

            UUID documentTypeId =
                    UUID.randomUUID();

            MultipartFile emptyFile =
                    new MockMultipartFile(
                            "documents",
                            "empty.pdf",
                            "application/pdf",
                            new byte[0]
                    );

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            assertThatThrownBy(() ->
                    applicationService.apply(
                            new ApplyApplicationRequest(
                                    "LOAN",
                                    loanScheme.getId(),
                                    new MultipartFile[]{
                                            emptyFile
                                    },
                                    new UUID[]{
                                            documentTypeId
                                    }
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "is empty"
                    );

            verify(
                    studentDocumentRepository,
                    never()
            ).save(any(StudentDocument.class));

            verify(
                    applicationDocumentRepository,
                    never()
            ).save(any(ApplicationDocument.class));
        }

        @Test
        @DisplayName("fails when document type does not exist")
        void apply_documentTypeNotFound() {
            LoanScheme loanScheme =
                    TestData.loanScheme(
                            student.getUser()
                    );

            UUID applicationId =
                    UUID.randomUUID();

            UUID documentTypeId =
                    UUID.randomUUID();

            MultipartFile uploadedFile =
                    createFile(
                            "aadhar.pdf",
                            "application/pdf"
                    );

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    loanScheme.getId()
            )).thenReturn(Optional.of(loanScheme));

            stubApplicationSave(applicationId);

            when(
                    studentDocumentRepository
                            .findFirstByStudent_IdAndDocumentType_IdOrderByCreatedAtDesc(
                                    student.getId(),
                                    documentTypeId
                            )
            ).thenReturn(Optional.empty());

            when(documentTypeRepository.findById(
                    documentTypeId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.apply(
                            new ApplyApplicationRequest(
                                    "LOAN",
                                    loanScheme.getId(),
                                    new MultipartFile[]{
                                            uploadedFile
                                    },
                                    new UUID[]{
                                            documentTypeId
                                    }
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Document type not found"
                    );

            verify(
                    fileStorageService,
                    never()
            ).store(any(MultipartFile.class));

            verify(
                    studentDocumentRepository,
                    never()
            ).save(any(StudentDocument.class));
        }

        @Test
        @DisplayName("fails when student profile does not exist")
        void apply_noStudentProfile() {
            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.apply(
                            new ApplyApplicationRequest(
                                    "LOAN",
                                    UUID.randomUUID(),
                                    null,
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Student profile not found"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }

        @Test
        @DisplayName("rejects invalid application type")
        void apply_unknownApplicationType() {
            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            assertThatThrownBy(() ->
                    applicationService.apply(
                            new ApplyApplicationRequest(
                                    "GRANT",
                                    UUID.randomUUID(),
                                    null,
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Invalid application type"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }

        @Test
        @DisplayName("fails when loan scheme does not exist")
        void apply_loanSchemeNotFound() {
            UUID schemeId =
                    UUID.randomUUID();

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(loanSchemeRepository.findById(
                    schemeId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.apply(
                            new ApplyApplicationRequest(
                                    "LOAN",
                                    schemeId,
                                    null,
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Loan scheme not found"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }

        @Test
        @DisplayName("fails when scholarship scheme does not exist")
        void apply_scholarshipSchemeNotFound() {
            UUID schemeId =
                    UUID.randomUUID();

            when(studentRepository.findByUserId(
                    principal.getUserId()
            )).thenReturn(Optional.of(student));

            when(
                    scholarshipSchemeRepository
                            .findById(schemeId)
            ).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.apply(
                            new ApplyApplicationRequest(
                                    "SCHOLARSHIP",
                                    schemeId,
                                    null,
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Scholarship scheme not found"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @Test
        @DisplayName("updates status, approved amount and history")
        void updateStatus_approve() {
            CustomUserPrincipal principal =
                    TestData.principal(RoleName.BANK);

            Application application =
                    TestData.application(
                            student,
                            ApplicationType.LOAN
                    );

            User actionBy =
                    TestData.user(RoleName.BANK);

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            when(applicationRepository.save(
                    any(Application.class)
            )).thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(Optional.of(actionBy));

            when(studentVerificationRepository.findByStudentId(
                    student.getId()
            )).thenReturn(Optional.empty());

            when(applicationDocumentRepository.findAllByApplicationId(
                    application.getId()
            )).thenReturn(List.of());

            ApplicationDetailResponse response =
                    applicationService.updateStatus(
                            new ApplicationActionRequest(
                                    application.getId(),
                                    "approved",
                                    "Sanctioned",
                                    450000.0
                            ),
                            principal
                    );

            assertThat(
                    response.applicationSummary()
                            .status()
            ).isEqualTo(
                    ApplicationStatus.APPROVED
            );

            assertThat(
                    response.applicationSummary()
                            .approvedAmount()
            ).isEqualByComparingTo(
                    BigDecimal.valueOf(450000.0)
            );

            ArgumentCaptor<ApplicationHistory> historyCaptor =
                    ArgumentCaptor.forClass(
                            ApplicationHistory.class
                    );

            verify(applicationHistoryRepository)
                    .save(historyCaptor.capture());

            ApplicationHistory capturedHistory =
                    historyCaptor.getValue();

            assertThat(capturedHistory.getStatus())
                    .isEqualTo(
                            ApplicationStatus.APPROVED
                    );

            assertThat(capturedHistory.getRemark())
                    .isEqualTo("Sanctioned");

            assertThat(capturedHistory.getActionByUser())
                    .isSameAs(actionBy);
        }

        @Test
        @DisplayName("keeps previous approved amount when amount is absent")
        void updateStatus_withoutApprovedAmount() {
            CustomUserPrincipal principal =
                    TestData.principal(
                            RoleName.GOVERNMENT
                    );

            Application application =
                    TestData.application(
                            student,
                            ApplicationType.SCHOLARSHIP
                    );

            BigDecimal previousApprovedAmount =
                    application.getApprovedAmount();

            User governmentUser =
                    TestData.user(RoleName.GOVERNMENT);

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            when(applicationRepository.save(
                    any(Application.class)
            )).thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(Optional.of(governmentUser));

            when(studentVerificationRepository.findByStudentId(
                    student.getId()
            )).thenReturn(Optional.empty());

            when(applicationDocumentRepository.findAllByApplicationId(
                    application.getId()
            )).thenReturn(List.of());

            ApplicationDetailResponse response =
                    applicationService.updateStatus(
                            new ApplicationActionRequest(
                                    application.getId(),
                                    "UNDER_REVIEW",
                                    "Checking documents",
                                    null
                            ),
                            principal
                    );

            assertThat(
                    response.applicationSummary()
                            .status()
            ).isEqualTo(
                    ApplicationStatus.UNDER_REVIEW
            );

            assertThat(
                    response.applicationSummary()
                            .approvedAmount()
            ).isEqualByComparingTo(
                    previousApprovedAmount
            );
        }

        @Test
        @DisplayName("fails when application does not exist")
        void updateStatus_applicationNotFound() {
            CustomUserPrincipal principal =
                    TestData.principal(RoleName.BANK);

            UUID applicationId =
                    UUID.randomUUID();

            when(applicationRepository.findApplicationById(
                    applicationId
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.updateStatus(
                            new ApplicationActionRequest(
                                    applicationId,
                                    "APPROVED",
                                    "ok",
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Application not found"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));
        }

        @Test
        @DisplayName("rejects invalid application status")
        void updateStatus_invalidStatus() {
            CustomUserPrincipal principal =
                    TestData.principal(RoleName.BANK);

            Application application =
                    TestData.application(
                            student,
                            ApplicationType.LOAN
                    );

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            assertThatThrownBy(() ->
                    applicationService.updateStatus(
                            new ApplicationActionRequest(
                                    application.getId(),
                                    "SANCTIONED",
                                    "ok",
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Invalid application status"
                    );

            verify(
                    applicationRepository,
                    never()
            ).save(any(Application.class));

            verify(
                    applicationHistoryRepository,
                    never()
            ).save(any(ApplicationHistory.class));
        }

        @Test
        @DisplayName("fails when acting user does not exist")
        void updateStatus_actingUserNotFound() {
            CustomUserPrincipal principal =
                    TestData.principal(RoleName.BANK);

            Application application =
                    TestData.application(
                            student,
                            ApplicationType.LOAN
                    );

            when(applicationRepository.findApplicationById(
                    application.getId()
            )).thenReturn(Optional.of(application));

            when(applicationRepository.save(
                    any(Application.class)
            )).thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

            when(userRepository.findById(
                    principal.getUserId()
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    applicationService.updateStatus(
                            new ApplicationActionRequest(
                                    application.getId(),
                                    "APPROVED",
                                    "ok",
                                    null
                            ),
                            principal
                    )
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(
                            "Authenticated user not found"
                    );

            verify(
                    applicationHistoryRepository,
                    never()
            ).save(any(ApplicationHistory.class));
        }
    }

    private MultipartFile createFile(
            String fileName,
            String contentType
    ) {
        return new MockMultipartFile(
                "documents",
                fileName,
                contentType,
                "test-file-content".getBytes()
        );
    }
}