package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.CreateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.ScholarshipEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.UpdateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.entity.Category;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.entity.ScholarshipBenefitDetail;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeCategory;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeProfession;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeRequiredDocument;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.PaymentFrequency;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.CategoryRepository;
import com.vidyasahay.vidyasahay.repository.DocumentTypeRepository;
import com.vidyasahay.vidyasahay.repository.ProfessionRepository;
import com.vidyasahay.vidyasahay.repository.StudentRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipBenefitDetailRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeCategoryRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeEligibilityRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeProfessionRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeRequiredDocumentRepository;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.support.SecurityContextSupport;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScholarshipSchemeServiceImpl")
class ScholarshipSchemeServiceImplTest {

    @Mock
    private ScholarshipSchemeRepository scholarshipSchemeRepository;

    @Mock
    private ScholarshipSchemeEligibilityRepository scholarshipSchemeEligibilityRepository;

    @Mock
    private ScholarshipBenefitDetailRepository scholarshipBenefitDetailRepository;

    @Mock
    private ScholarshipSchemeRequiredDocumentRepository scholarshipSchemeRequiredDocumentRepository;

    @Mock
    private ScholarshipSchemeProfessionRepository scholarshipSchemeProfessionRepository;

    @Mock
    private ScholarshipSchemeCategoryRepository scholarshipSchemeCategoryRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private ProfessionRepository professionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ScholarshipSchemeServiceImpl scholarshipSchemeService;

    private User governmentUser;
    private CustomUserPrincipal governmentPrincipal;

    @BeforeEach
    void setUp() {
        governmentUser = TestData.user(RoleName.GOVERNMENT);
        governmentPrincipal = TestData.principal(governmentUser.getId(), RoleName.GOVERNMENT);
    }

    @AfterEach
    void tearDown() {
        SecurityContextSupport.clear();
    }

    private ScholarshipSchemeEligibility eligibility(ScholarshipScheme scheme) {
        ScholarshipSchemeEligibility eligibility = new ScholarshipSchemeEligibility();
        eligibility.setId(UUID.randomUUID());
        eligibility.setScholarshipScheme(scheme);
        eligibility.setMinimumAge(17);
        eligibility.setMaximumAge(30);
        eligibility.setMaximumAnnualFamilyIncome(new BigDecimal("500000"));
        eligibility.setMinimumPercentageCriteria(new BigDecimal("60.00"));
        return eligibility;
    }

    private ScholarshipBenefitDetail benefit(ScholarshipScheme scheme) {
        ScholarshipBenefitDetail benefit = new ScholarshipBenefitDetail();
        benefit.setId(UUID.randomUUID());
        benefit.setScholarshipScheme(scheme);
        benefit.setScholarshipAmount(new BigDecimal("40000"));
        benefit.setAmountType(ScholarshipAmountType.FIXED_AMOUNT);
        benefit.setPaymentFrequency(PaymentFrequency.YEARLY);
        benefit.setTotalSchemeBudget(new BigDecimal("10000000"));
        return benefit;
    }

    @Nested
    @DisplayName("getAllScholarshipSchemes")
    class GetAllScholarshipSchemes {

        @Test
        @DisplayName("maps every scheme to a summary")
        void getAllScholarshipSchemes_success() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            when(scholarshipSchemeRepository.findAll()).thenReturn(List.of(scheme));

            List<ScholarshipSchemeSummaryResponseDTO> response =
                    scholarshipSchemeService.getAllScholarshipSchemes();

            assertThat(response).hasSize(1);
            assertThat(response.get(0).scholarshipSchemeId()).isEqualTo(scheme.getId());
            assertThat(response.get(0).scholarshipName()).isEqualTo(scheme.getName());
            assertThat(response.get(0).scholarshipType()).isEqualTo("MERIT_BASED");
            assertThat(response.get(0).status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("fails when no scheme exists")
        void getAllScholarshipSchemes_empty() {
            when(scholarshipSchemeRepository.findAll()).thenReturn(List.of());

            assertThatThrownBy(() -> scholarshipSchemeService.getAllScholarshipSchemes())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No scholarship schemes found");
        }
    }

    @Nested
    @DisplayName("getScholarshipSchemeById")
    class GetScholarshipSchemeById {

        @Test
        @DisplayName("assembles the scheme with eligibility, benefits, documents, professions and categories")
        void getScholarshipSchemeById_success() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            DocumentType documentType = TestData.documentType();
            ScholarshipSchemeRequiredDocument requiredDocument =
                    new ScholarshipSchemeRequiredDocument();
            requiredDocument.setScholarshipScheme(scheme);
            requiredDocument.setDocumentType(documentType);

            Profession profession = TestData.profession();
            ScholarshipSchemeProfession schemeProfession = new ScholarshipSchemeProfession();
            schemeProfession.setScholarshipScheme(scheme);
            schemeProfession.setProfession(profession);

            Category category = TestData.category();
            ScholarshipSchemeCategory schemeCategory = new ScholarshipSchemeCategory();
            schemeCategory.setScholarshipScheme(scheme);
            schemeCategory.setCategory(category);

            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));
            when(scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(eligibility(scheme)));
            when(scholarshipBenefitDetailRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(benefit(scheme)));
            when(scholarshipSchemeRequiredDocumentRepository.findByScholarshipSchemeId(scheme.getId()))
                    .thenReturn(List.of(requiredDocument));
            when(scholarshipSchemeProfessionRepository.findByScholarshipSchemeId(scheme.getId()))
                    .thenReturn(List.of(schemeProfession));
            when(scholarshipSchemeCategoryRepository.findByScholarshipSchemeId(scheme.getId()))
                    .thenReturn(List.of(schemeCategory));

            ScholarshipSchemeDetailedResponseDTO response =
                    scholarshipSchemeService.getScholarshipSchemeById(scheme.getId());

            assertThat(response.scholarshipSchemeId()).isEqualTo(scheme.getId());
            assertThat(response.applierMinAge()).isEqualTo(17);
            assertThat(response.applierMaxAge()).isEqualTo(30);
            assertThat(response.maxFamilyAnnualIncome()).isEqualTo(500_000L);
            assertThat(response.minPercentageCriteria()).isEqualTo(60.0);
            assertThat(response.requiredDocuments()).containsExactly("Aadhaar Card");
            assertThat(response.eligibleProfessions()).containsExactly("Engineering");
            assertThat(response.eligibleCategories()).containsExactly("GEN");
            assertThat(response.scholarshipAmount()).isEqualTo(40_000.0);
            assertThat(response.amountType()).isEqualTo("FIXED_AMOUNT");
            assertThat(response.paymentFrequency()).isEqualTo("YEARLY");
        }

        @Test
        @DisplayName("defaults the nullable eligibility numbers to zero")
        void getScholarshipSchemeById_nullEligibilityNumbers() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            ScholarshipSchemeEligibility sparse = new ScholarshipSchemeEligibility();
            sparse.setScholarshipScheme(scheme);
            sparse.setMinimumAge(null);
            sparse.setMaximumAge(null);
            sparse.setMaximumAnnualFamilyIncome(null);
            sparse.setMinimumPercentageCriteria(null);

            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));
            when(scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(sparse));
            when(scholarshipBenefitDetailRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(benefit(scheme)));
            when(scholarshipSchemeRequiredDocumentRepository.findByScholarshipSchemeId(scheme.getId()))
                    .thenReturn(List.of());
            when(scholarshipSchemeProfessionRepository.findByScholarshipSchemeId(scheme.getId()))
                    .thenReturn(List.of());
            when(scholarshipSchemeCategoryRepository.findByScholarshipSchemeId(scheme.getId()))
                    .thenReturn(List.of());

            ScholarshipSchemeDetailedResponseDTO response =
                    scholarshipSchemeService.getScholarshipSchemeById(scheme.getId());

            assertThat(response.applierMinAge()).isZero();
            assertThat(response.maxFamilyAnnualIncome()).isZero();
            assertThat(response.minPercentageCriteria()).isZero();
        }

        @Test
        @DisplayName("fails for an unknown scheme id")
        void getScholarshipSchemeById_notFound() {
            UUID schemeId = UUID.randomUUID();

            when(scholarshipSchemeRepository.findById(schemeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getScholarshipSchemeById(schemeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Scholarship scheme not found");
        }

        @Test
        @DisplayName("fails when the eligibility row is missing")
        void getScholarshipSchemeById_eligibilityMissing() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));
            when(scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getScholarshipSchemeById(scheme.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Eligibility details not found");
        }

        @Test
        @DisplayName("fails when the benefit row is missing")
        void getScholarshipSchemeById_benefitMissing() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));
            when(scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(eligibility(scheme)));
            when(scholarshipBenefitDetailRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getScholarshipSchemeById(scheme.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Benefit details not found");
        }
    }

    @Nested
    @DisplayName("getScholarshipSchemesCreatedByMe")
    class GetScholarshipSchemesCreatedByMe {

        @Test
        @DisplayName("returns the schemes created by the authenticated government user")
        void createdByMe_success() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            SecurityContextSupport.authenticate(governmentPrincipal);
            when(scholarshipSchemeRepository.findByCreatedById(governmentPrincipal.getUserId()))
                    .thenReturn(List.of(scheme));

            assertThat(scholarshipSchemeService.getScholarshipSchemesCreatedByMe()).hasSize(1);
        }

        @Test
        @DisplayName("refuses a non-government user")
        void createdByMe_wrongRole() {
            SecurityContextSupport.authenticate(TestData.principal(RoleName.BANK));

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getScholarshipSchemesCreatedByMe())
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only government users");
        }

        @Test
        @DisplayName("fails when the user has not created any scheme")
        void createdByMe_empty() {
            SecurityContextSupport.authenticate(governmentPrincipal);
            when(scholarshipSchemeRepository.findByCreatedById(governmentPrincipal.getUserId()))
                    .thenReturn(List.of());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getScholarshipSchemesCreatedByMe())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No scholarship schemes found");
        }
    }

    @Nested
    @DisplayName("getEligibleScholarshipSchemes")
    class GetEligibleScholarshipSchemes {

        private final ScholarshipEligibilityRequestDTO request =
                new ScholarshipEligibilityRequestDTO(
                        new BigDecimal("300000"), new BigDecimal("78.50"));

        @Test
        @DisplayName("keeps only schemes matching both the category and the profession")
        void eligible_success() {
            Student student = TestData.student();
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            ScholarshipSchemeProfession professionMapping = new ScholarshipSchemeProfession();
            professionMapping.setScholarshipScheme(scheme);
            professionMapping.setProfession(student.getCourse().getProfession());

            ScholarshipSchemeCategory categoryMapping = new ScholarshipSchemeCategory();
            categoryMapping.setScholarshipScheme(scheme);
            categoryMapping.setCategory(student.getCategory());

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));
            when(scholarshipSchemeProfessionRepository.findByProfessionId(
                    student.getCourse().getProfession().getId()))
                    .thenReturn(List.of(professionMapping));
            when(scholarshipSchemeCategoryRepository.findByCategoryId(student.getCategory().getId()))
                    .thenReturn(Set.of(categoryMapping));
            when(scholarshipSchemeEligibilityRepository
                    .findByMinimumPercentageCriteriaLessThanEqualAndMaximumAnnualFamilyIncomeGreaterThanEqual(
                            request.academicPercentage(), request.annualFamilyIncome()))
                    .thenReturn(List.of(eligibility(scheme)));

            List<ScholarshipSchemeSummaryResponseDTO> response =
                    scholarshipSchemeService.getEligibleScholarshipSchemes(request);

            assertThat(response).hasSize(1);
            assertThat(response.get(0).scholarshipSchemeId()).isEqualTo(scheme.getId());
        }

        @Test
        @DisplayName("fails when the caller has no student profile")
        void eligible_studentNotFound() {
            CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getEligibleScholarshipSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student not found");
        }

        @Test
        @DisplayName("fails when the student has no category")
        void eligible_noCategory() {
            Student student = TestData.student();
            student.setCategory(null);
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getEligibleScholarshipSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category is not configured");
        }

        @Test
        @DisplayName("fails when the student's course has no profession")
        void eligible_noProfession() {
            Student student = TestData.student();
            student.getCourse().setProfession(null);
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getEligibleScholarshipSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profession is not configured");
        }

        @Test
        @DisplayName("fails when no scheme covers the student's profession")
        void eligible_noProfessionScheme() {
            Student student = TestData.student();
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));
            when(scholarshipSchemeProfessionRepository.findByProfessionId(
                    student.getCourse().getProfession().getId())).thenReturn(List.of());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getEligibleScholarshipSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("student's profession");
        }

        @Test
        @DisplayName("fails when no scheme covers the student's category")
        void eligible_noCategoryScheme() {
            Student student = TestData.student();
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            ScholarshipSchemeProfession professionMapping = new ScholarshipSchemeProfession();
            professionMapping.setScholarshipScheme(scheme);
            professionMapping.setProfession(student.getCourse().getProfession());

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));
            when(scholarshipSchemeProfessionRepository.findByProfessionId(
                    student.getCourse().getProfession().getId()))
                    .thenReturn(List.of(professionMapping));
            when(scholarshipSchemeCategoryRepository.findByCategoryId(student.getCategory().getId()))
                    .thenReturn(Set.of());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.getEligibleScholarshipSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("student's category");
        }
    }

    @Nested
    @DisplayName("createScholarshipScheme")
    class CreateScholarshipScheme {

        private CreateScholarshipSchemeRequestDTO request(
                LocalDate start,
                LocalDate end,
                int minAge,
                int maxAge,
                long maxIncome,
                double minPercentage,
                double amount,
                double budget) {

            return new CreateScholarshipSchemeRequestDTO(
                    "National Merit Scholarship",
                    ScholarshipType.MERIT_BASED,
                    "2025-26",
                    start,
                    end,
                    minAge,
                    maxAge,
                    maxIncome,
                    minPercentage,
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    amount,
                    ScholarshipAmountType.FIXED_AMOUNT,
                    PaymentFrequency.YEARLY,
                    budget);
        }

        private CreateScholarshipSchemeRequestDTO validRequest() {
            return request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    17, 30, 500_000L, 60.0, 40_000.0, 10_000_000.0);
        }

        @Test
        @DisplayName("persists the scheme with its eligibility and benefit rows")
        void createScholarshipScheme_success() {
            UUID schemeId = UUID.randomUUID();

            SecurityContextSupport.authenticate(governmentPrincipal);
            when(userRepository.findById(governmentPrincipal.getUserId()))
                    .thenReturn(Optional.of(governmentUser));
            when(scholarshipSchemeRepository.save(any(ScholarshipScheme.class)))
                    .thenAnswer(invocation -> {
                        ScholarshipScheme saved = invocation.getArgument(0);
                        saved.setId(schemeId);
                        return saved;
                    });

            UUID result = scholarshipSchemeService.createScholarshipScheme(validRequest());

            assertThat(result).isEqualTo(schemeId);

            verify(scholarshipSchemeEligibilityRepository)
                    .save(any(ScholarshipSchemeEligibility.class));
            verify(scholarshipBenefitDetailRepository)
                    .save(any(ScholarshipBenefitDetail.class));
        }

        @Test
        @DisplayName("marks a newly created scheme as ACTIVE and records the creator")
        void createScholarshipScheme_defaults() {
            SecurityContextSupport.authenticate(governmentPrincipal);
            when(userRepository.findById(governmentPrincipal.getUserId()))
                    .thenReturn(Optional.of(governmentUser));
            when(scholarshipSchemeRepository.save(any(ScholarshipScheme.class)))
                    .thenAnswer(invocation -> {
                        ScholarshipScheme saved = invocation.getArgument(0);
                        saved.setId(UUID.randomUUID());
                        assertThat(saved.getStatus()).isEqualTo(SchemeStatus.ACTIVE);
                        assertThat(saved.getCreatedBy()).isSameAs(governmentUser);
                        assertThat(saved.getUpdatedBy()).isSameAs(governmentUser);
                        return saved;
                    });

            scholarshipSchemeService.createScholarshipScheme(validRequest());

            verify(scholarshipSchemeRepository).save(any(ScholarshipScheme.class));
        }

        @Test
        @DisplayName("rejects a start date after the end date")
        void createScholarshipScheme_invalidDates() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now().plusMonths(6), LocalDate.now(),
                    17, 30, 500_000L, 60.0, 40_000.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Start date");

            verify(scholarshipSchemeRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a negative minimum age")
        void createScholarshipScheme_negativeAge() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    -1, 30, 500_000L, 60.0, 40_000.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Minimum age");
        }

        @Test
        @DisplayName("rejects a maximum age below the minimum")
        void createScholarshipScheme_invertedAges() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    35, 30, 500_000L, 60.0, 40_000.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Maximum age");
        }

        @Test
        @DisplayName("rejects a negative maximum family income")
        void createScholarshipScheme_negativeIncome() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    17, 30, -1L, 60.0, 40_000.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("annual family income");
        }

        @Test
        @DisplayName("rejects a percentage criterion outside 0-100")
        void createScholarshipScheme_percentageOutOfRange() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    17, 30, 500_000L, 120.0, 40_000.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("between 0 and 100");
        }

        @Test
        @DisplayName("rejects a negative scholarship amount")
        void createScholarshipScheme_negativeAmount() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    17, 30, 500_000L, 60.0, -1.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Scholarship amount cannot be negative");
        }

        @Test
        @DisplayName("rejects a negative total budget")
        void createScholarshipScheme_negativeBudget() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    17, 30, 500_000L, 60.0, 40_000.0, -1.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Total budget");
        }

        @Test
        @DisplayName("requires a non-zero amount for a FIXED_AMOUNT scheme")
        void createScholarshipScheme_fixedAmountZero() {
            assertThatThrownBy(() -> scholarshipSchemeService.createScholarshipScheme(request(
                    LocalDate.now(), LocalDate.now().plusMonths(6),
                    17, 30, 500_000L, 60.0, 0.0, 10_000_000.0)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("FIXED_AMOUNT");
        }

        @Test
        @DisplayName("fails when the authenticated user row is missing")
        void createScholarshipScheme_userNotFound() {
            SecurityContextSupport.authenticate(governmentPrincipal);
            when(userRepository.findById(governmentPrincipal.getUserId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.createScholarshipScheme(validRequest()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found");
        }
    }

    @Nested
    @DisplayName("updateScholarshipScheme")
    class UpdateScholarshipScheme {

        private UpdateScholarshipSchemeRequestDTO patch(String name, SchemeStatus status) {
            return new UpdateScholarshipSchemeRequestDTO(
                    name, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, status);
        }

        private UpdateScholarshipSchemeRequestDTO emptyPatch() {
            return patch(null, null);
        }

        @Test
        @DisplayName("applies a partial patch and re-saves the scheme, eligibility and benefit")
        void updateScholarshipScheme_success() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            SecurityContextSupport.authenticate(governmentPrincipal);
            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));
            when(scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(eligibility(scheme)));
            when(scholarshipBenefitDetailRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(benefit(scheme)));

            scholarshipSchemeService.updateScholarshipScheme(
                    scheme.getId(), patch("Renamed Scholarship", SchemeStatus.INACTIVE));

            assertThat(scheme.getName()).isEqualTo("Renamed Scholarship");
            assertThat(scheme.getStatus()).isEqualTo(SchemeStatus.INACTIVE);

            verify(scholarshipSchemeRepository).save(scheme);
            verify(scholarshipSchemeEligibilityRepository)
                    .save(any(ScholarshipSchemeEligibility.class));
            verify(scholarshipBenefitDetailRepository)
                    .save(any(ScholarshipBenefitDetail.class));
        }

        @Test
        @DisplayName("fails for an unknown scheme id")
        void updateScholarshipScheme_notFound() {
            UUID schemeId = UUID.randomUUID();

            when(scholarshipSchemeRepository.findById(schemeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    scholarshipSchemeService.updateScholarshipScheme(schemeId, emptyPatch()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Scholarship scheme not found");
        }

        @Test
        @DisplayName("refuses a government user who did not create the scheme")
        void updateScholarshipScheme_notTheCreator() {
            ScholarshipScheme scheme =
                    TestData.scholarshipScheme(TestData.user(RoleName.GOVERNMENT));

            SecurityContextSupport.authenticate(governmentPrincipal);
            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));

            assertThatThrownBy(() ->
                    scholarshipSchemeService.updateScholarshipScheme(scheme.getId(), emptyPatch()))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("not authorized");

            verify(scholarshipSchemeRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a patch that inverts the scheme dates")
        void updateScholarshipScheme_invertedDates() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            UpdateScholarshipSchemeRequestDTO datePatch = new UpdateScholarshipSchemeRequestDTO(
                    null, null, null, LocalDate.now().plusYears(2), LocalDate.now(),
                    null, null, null, null, null, null, null, null, null, null, null, null);

            SecurityContextSupport.authenticate(governmentPrincipal);
            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));

            assertThatThrownBy(() ->
                    scholarshipSchemeService.updateScholarshipScheme(scheme.getId(), datePatch))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Start date");
        }

        @Test
        @DisplayName("rejects a patch where the minimum age exceeds the maximum age")
        void updateScholarshipScheme_invertedAges() {
            ScholarshipScheme scheme = TestData.scholarshipScheme(governmentUser);

            UpdateScholarshipSchemeRequestDTO agePatch = new UpdateScholarshipSchemeRequestDTO(
                    null, null, null, null, null, 40, 20, null, null,
                    null, null, null, null, null, null, null, null);

            SecurityContextSupport.authenticate(governmentPrincipal);
            when(scholarshipSchemeRepository.findById(scheme.getId()))
                    .thenReturn(Optional.of(scheme));
            when(scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme))
                    .thenReturn(Optional.of(eligibility(scheme)));

            assertThatThrownBy(() ->
                    scholarshipSchemeService.updateScholarshipScheme(scheme.getId(), agePatch))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Minimum age cannot exceed");
        }
    }
}
