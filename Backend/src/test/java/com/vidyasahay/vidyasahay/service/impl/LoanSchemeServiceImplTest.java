package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.loanScheme.CreateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.LoanEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.UpdateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.entity.Bank;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.LoanSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.LoanSchemeMoratorium;
import com.vidyasahay.vidyasahay.entity.LoanSchemeProfession;
import com.vidyasahay.vidyasahay.entity.LoanSchemeRepaymentRule;
import com.vidyasahay.vidyasahay.entity.LoanSchemeRequiredDocument;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.DisbursementType;
import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.BankRepository;
import com.vidyasahay.vidyasahay.repository.DocumentTypeRepository;
import com.vidyasahay.vidyasahay.repository.ProfessionRepository;
import com.vidyasahay.vidyasahay.repository.StudentRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeEligibilityRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeMoratoriumRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeProfessionRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRepaymentRuleRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRequiredDocumentRepository;
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
import org.springframework.data.domain.Sort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoanSchemeServiceImpl")
class LoanSchemeServiceImplTest {

    @Mock
    private LoanSchemeRepository loanSchemeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanSchemeProfessionRepository loanSchemeProfessionRepository;

    @Mock
    private LoanSchemeEligibilityRepository loanSchemeEligibilityRepository;

    @Mock
    private LoanSchemeMoratoriumRepository loanSchemeMoratoriumRepository;

    @Mock
    private LoanSchemeRepaymentRuleRepository loanSchemeRepaymentRuleRepository;

    @Mock
    private LoanSchemeRequiredDocumentRepository loanSchemeRequiredDocumentRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private ProfessionRepository professionRepository;

    @InjectMocks
    private LoanSchemeServiceImpl loanSchemeService;

    private User bankUser;
    private CustomUserPrincipal bankPrincipal;

    @BeforeEach
    void setUp() {
        bankUser = TestData.user(RoleName.BANK);
        bankPrincipal = TestData.principal(bankUser.getId(), RoleName.BANK);
    }

    @AfterEach
    void tearDown() {
        SecurityContextSupport.clear();
    }

    /** Stubs the SecurityContextHolder lookup plus the BANK role check. */
    private void authenticateAsBank() {
        SecurityContextSupport.authenticate(bankPrincipal);
        when(userRepository.findById(bankUser.getId())).thenReturn(Optional.of(bankUser));
    }

    @Nested
    @DisplayName("getAllLoanSchemes")
    class GetAllLoanSchemes {

        @Test
        @DisplayName("maps every scheme to a summary, newest first")
        void getAllLoanSchemes_success() {
            LoanScheme scheme = TestData.loanScheme(bankUser);

            when(loanSchemeRepository.findAll(any(Sort.class))).thenReturn(List.of(scheme));

            List<LoanSchemeSummaryResponseDTO> response = loanSchemeService.getAllLoanSchemes();

            assertThat(response).hasSize(1);
            assertThat(response.get(0).loanSchemeId()).isEqualTo(scheme.getId());
            assertThat(response.get(0).schemeName()).isEqualTo(scheme.getName());
            assertThat(response.get(0).minAmount()).isEqualTo(50_000);
            assertThat(response.get(0).maxAmount()).isEqualTo(1_000_000);
            assertThat(response.get(0).status()).isEqualTo(SchemeStatus.ACTIVE);
        }

        @Test
        @DisplayName("fails when the portal has no loan schemes")
        void getAllLoanSchemes_empty() {
            when(loanSchemeRepository.findAll(any(Sort.class))).thenReturn(List.of());

            assertThatThrownBy(() -> loanSchemeService.getAllLoanSchemes())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No loan schemes");
        }

        @Test
        @DisplayName("fails loudly when an amount cannot be represented as a whole number")
        void getAllLoanSchemes_fractionalAmount() {
            LoanScheme scheme = TestData.loanScheme(bankUser);
            scheme.setMinLoanAmount(new BigDecimal("50000.55"));

            when(loanSchemeRepository.findAll(any(Sort.class))).thenReturn(List.of(scheme));

            assertThatThrownBy(() -> loanSchemeService.getAllLoanSchemes())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("whole-number");
        }
    }

    @Nested
    @DisplayName("getLoanSchemeById")
    class GetLoanSchemeById {

        private LoanScheme scheme;

        @BeforeEach
        void setUp() {
            scheme = TestData.loanScheme(bankUser);
        }

        private LoanSchemeEligibility eligibility() {
            LoanSchemeEligibility eligibility = new LoanSchemeEligibility();
            eligibility.setId(UUID.randomUUID());
            eligibility.setLoanScheme(scheme);
            eligibility.setMinAge(18);
            eligibility.setMaxAge(35);
            eligibility.setCoBorrowerRequired(true);
            eligibility.setMinCreditScore(700);
            return eligibility;
        }

        private LoanSchemeMoratorium moratorium() {
            LoanSchemeMoratorium moratorium = new LoanSchemeMoratorium();
            moratorium.setId(UUID.randomUUID());
            moratorium.setLoanScheme(scheme);
            moratorium.setCoursePeriodIncluded(true);
            moratorium.setAdditionalMonths(6);
            return moratorium;
        }

        private LoanSchemeRepaymentRule repaymentRule() {
            LoanSchemeRepaymentRule rule = new LoanSchemeRepaymentRule();
            rule.setId(UUID.randomUUID());
            rule.setLoanScheme(scheme);
            rule.setMinTenureYears(2);
            rule.setMaxTenureYears(10);
            rule.setPrepaymentAllowed(true);
            rule.setForeclosureCharges(new BigDecimal("2.50"));
            return rule;
        }

        @Test
        @DisplayName("assembles the scheme with eligibility, moratorium, repayment, docs and professions")
        void getLoanSchemeById_success() {
            DocumentType documentType = TestData.documentType();
            Profession profession = TestData.profession();

            LoanSchemeRequiredDocument requiredDocument = new LoanSchemeRequiredDocument();
            requiredDocument.setLoanScheme(scheme);
            requiredDocument.setDocumentType(documentType);

            LoanSchemeProfession schemeProfession = new LoanSchemeProfession();
            schemeProfession.setLoanScheme(scheme);
            schemeProfession.setProfession(profession);

            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            when(loanSchemeEligibilityRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(eligibility()));
            when(loanSchemeMoratoriumRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(moratorium()));
            when(loanSchemeRepaymentRuleRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(repaymentRule()));
            when(loanSchemeRequiredDocumentRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(List.of(requiredDocument));
            when(loanSchemeProfessionRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(List.of(schemeProfession));

            LoanSchemeDetailedResponseDTO response =
                    loanSchemeService.getLoanSchemeById(scheme.getId());

            assertThat(response.loanSchemeId()).isEqualTo(scheme.getId());
            assertThat(response.bankId()).isEqualTo(scheme.getBank().getId());
            assertThat(response.applierMinAge()).isEqualTo(18);
            assertThat(response.coBorrowerRequired()).isTrue();
            assertThat(response.minCreditScore()).isEqualTo(700);
            assertThat(response.requiredDocumentIds()).containsExactly("Aadhaar Card");
            assertThat(response.eligibleProfessionIds()).containsExactly("Engineering");
            assertThat(response.minTenureForRepayment()).isEqualTo(2);
            assertThat(response.prepaymentAllowed()).isTrue();
            assertThat(response.coursePeriodIncluded()).isTrue();
            assertThat(response.additionalMonths()).isEqualTo(6);
        }

        @Test
        @DisplayName("fails for an unknown scheme id")
        void getLoanSchemeById_schemeNotFound() {
            UUID schemeId = UUID.randomUUID();

            when(loanSchemeRepository.findById(schemeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemeById(schemeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Loan scheme not found");
        }

        @Test
        @DisplayName("fails when the eligibility row is missing")
        void getLoanSchemeById_eligibilityMissing() {
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            when(loanSchemeEligibilityRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemeById(scheme.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Eligibility details");
        }

        @Test
        @DisplayName("fails when the moratorium row is missing")
        void getLoanSchemeById_moratoriumMissing() {
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            when(loanSchemeEligibilityRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(eligibility()));
            when(loanSchemeMoratoriumRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemeById(scheme.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Moratorium details");
        }

        @Test
        @DisplayName("fails when the repayment rule row is missing")
        void getLoanSchemeById_repaymentMissing() {
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            when(loanSchemeEligibilityRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(eligibility()));
            when(loanSchemeMoratoriumRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(moratorium()));
            when(loanSchemeRepaymentRuleRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemeById(scheme.getId()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Repayment details");
        }
    }

    @Nested
    @DisplayName("getLoanSchemesCreatedByMe")
    class GetLoanSchemesCreatedByMe {

        @Test
        @DisplayName("returns the schemes created by the authenticated bank user")
        void getLoanSchemesCreatedByMe_success() {
            Bank bank = TestData.bank();
            LoanScheme scheme = TestData.loanScheme(bankUser);

            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(bank));
            when(loanSchemeRepository.findByBankIdAndCreatedByIdOrderByCreatedAtDesc(
                    bank.getId(), bankUser.getId())).thenReturn(List.of(scheme));

            assertThat(loanSchemeService.getLoanSchemesCreatedByMe()).hasSize(1);
        }

        @Test
        @DisplayName("fails when the user has no bank profile")
        void getLoanSchemesCreatedByMe_noBankProfile() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemesCreatedByMe())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Bank profile not found");
        }

        @Test
        @DisplayName("fails when the bank user has not created any scheme")
        void getLoanSchemesCreatedByMe_empty() {
            Bank bank = TestData.bank();

            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(bank));
            when(loanSchemeRepository.findByBankIdAndCreatedByIdOrderByCreatedAtDesc(
                    bank.getId(), bankUser.getId())).thenReturn(List.of());

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemesCreatedByMe())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No loan schemes created");
        }

        @Test
        @DisplayName("refuses a non-bank user")
        void getLoanSchemesCreatedByMe_notABankUser() {
            User governmentUser = TestData.user(RoleName.GOVERNMENT);
            CustomUserPrincipal principal =
                    TestData.principal(governmentUser.getId(), RoleName.GOVERNMENT);

            SecurityContextSupport.authenticate(principal);
            when(userRepository.findById(governmentUser.getId()))
                    .thenReturn(Optional.of(governmentUser));

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemesCreatedByMe())
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only bank users");
        }

        @Test
        @DisplayName("fails when there is no authentication in the security context")
        void getLoanSchemesCreatedByMe_noAuthentication() {
            SecurityContextSupport.clear();

            assertThatThrownBy(() -> loanSchemeService.getLoanSchemesCreatedByMe())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Authenticated user not found");
        }
    }

    @Nested
    @DisplayName("getEligibleLoanSchemes")
    class GetEligibleLoanSchemes {

        private final LoanEligibilityRequestDTO request =
                new LoanEligibilityRequestDTO(new BigDecimal("400000"));

        @Test
        @DisplayName("returns only active schemes that also match the student's profession")
        void getEligibleLoanSchemes_success() {
            Student student = TestData.student();
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);
            LoanScheme scheme = TestData.loanScheme(bankUser);

            LoanSchemeProfession mapping = new LoanSchemeProfession();
            mapping.setLoanScheme(scheme);
            mapping.setProfession(student.getCourse().getProfession());

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));
            when(loanSchemeProfessionRepository.findByProfessionId(
                    student.getCourse().getProfession().getId()))
                    .thenReturn(List.of(mapping));
            when(loanSchemeRepository
                    .findByMaxLoanAmountGreaterThanEqualAndStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                            eq(request.requiredLoanAmount()),
                            eq(SchemeStatus.ACTIVE),
                            any(LocalDate.class),
                            any(LocalDate.class)))
                    .thenReturn(List.of(scheme));

            List<LoanSchemeSummaryResponseDTO> response =
                    loanSchemeService.getEligibleLoanSchemes(request);

            assertThat(response).hasSize(1);
            assertThat(response.get(0).loanSchemeId()).isEqualTo(scheme.getId());
        }

        @Test
        @DisplayName("fails when the caller has no student profile")
        void getEligibleLoanSchemes_studentNotFound() {
            CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> loanSchemeService.getEligibleLoanSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Student not found");
        }

        @Test
        @DisplayName("fails when the student's course has no profession")
        void getEligibleLoanSchemes_noProfession() {
            Student student = TestData.student();
            student.getCourse().setProfession(null);
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));

            assertThatThrownBy(() -> loanSchemeService.getEligibleLoanSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profession is not configured");
        }

        @Test
        @DisplayName("fails when no scheme covers the student's profession")
        void getEligibleLoanSchemes_noSchemeForProfession() {
            Student student = TestData.student();
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));
            when(loanSchemeProfessionRepository.findByProfessionId(
                    student.getCourse().getProfession().getId())).thenReturn(List.of());

            assertThatThrownBy(() -> loanSchemeService.getEligibleLoanSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("student's profession");
        }

        @Test
        @DisplayName("fails when the amount and date filters leave nothing")
        void getEligibleLoanSchemes_noneAfterFiltering() {
            Student student = TestData.student();
            CustomUserPrincipal principal =
                    TestData.principal(student.getUser().getId(), RoleName.STUDENT);
            LoanScheme scheme = TestData.loanScheme(bankUser);

            LoanSchemeProfession mapping = new LoanSchemeProfession();
            mapping.setLoanScheme(scheme);
            mapping.setProfession(student.getCourse().getProfession());

            SecurityContextSupport.authenticate(principal);
            when(studentRepository.findByUserId(principal.getUserId()))
                    .thenReturn(Optional.of(student));
            when(loanSchemeProfessionRepository.findByProfessionId(
                    student.getCourse().getProfession().getId())).thenReturn(List.of(mapping));
            when(loanSchemeRepository
                    .findByMaxLoanAmountGreaterThanEqualAndStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                            any(BigDecimal.class),
                            any(SchemeStatus.class),
                            any(LocalDate.class),
                            any(LocalDate.class)))
                    .thenReturn(List.of());

            assertThatThrownBy(() -> loanSchemeService.getEligibleLoanSchemes(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No eligible loan schemes");
        }
    }

    @Nested
    @DisplayName("createLoanScheme")
    class CreateLoanScheme {

        private CreateLoanSchemeRequestDTO request(
                LocalDate from,
                LocalDate to,
                BigDecimal minAmount,
                BigDecimal maxAmount,
                InterestType interestType,
                BigDecimal minRate,
                BigDecimal maxRate,
                int minAge,
                int maxAge,
                Boolean coBorrower,
                Integer creditScore,
                int minTenure,
                int maxTenure,
                Boolean prepayment,
                BigDecimal foreclosure) {

            return new CreateLoanSchemeRequestDTO(
                    "  Vidya Education Loan  ",
                    from,
                    to,
                    minAmount,
                    maxAmount,
                    interestType,
                    minRate,
                    maxRate,
                    DisbursementType.YEARLY,
                    minAge,
                    maxAge,
                    coBorrower,
                    creditScore,
                    Set.of(),
                    Set.of(),
                    minTenure,
                    maxTenure,
                    prepayment,
                    foreclosure,
                    Boolean.TRUE,
                    6);
        }

        private CreateLoanSchemeRequestDTO validRequest() {
            return request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FLOATING, new BigDecimal("8.5"), new BigDecimal("12.5"),
                    18, 35, Boolean.TRUE, 700, 2, 10, Boolean.TRUE, new BigDecimal("2.5"));
        }

        @Test
        @DisplayName("persists the scheme plus its eligibility, repayment and moratorium rows")
        void createLoanScheme_success() {
            Bank bank = TestData.bank();
            UUID schemeId = UUID.randomUUID();

            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(bank));
            when(loanSchemeRepository.save(any(LoanScheme.class))).thenAnswer(invocation -> {
                LoanScheme saved = invocation.getArgument(0);
                saved.setId(schemeId);
                return saved;
            });

            UUID result = loanSchemeService.createLoanScheme(validRequest());

            assertThat(result).isEqualTo(schemeId);

            verify(loanSchemeEligibilityRepository).save(any(LoanSchemeEligibility.class));
            verify(loanSchemeRepaymentRuleRepository).save(any(LoanSchemeRepaymentRule.class));
            verify(loanSchemeMoratoriumRepository).save(any(LoanSchemeMoratorium.class));
        }

        @Test
        @DisplayName("mirrors the maximum rate into the minimum rate for a FIXED scheme")
        void createLoanScheme_fixedInterest() {
            Bank bank = TestData.bank();

            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(bank));
            when(loanSchemeRepository.save(any(LoanScheme.class))).thenAnswer(invocation -> {
                LoanScheme saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                assertThat(saved.getMinimumRate()).isEqualByComparingTo("11.0");
                assertThat(saved.getMaximumRate()).isEqualByComparingTo("11.0");
                return saved;
            });

            loanSchemeService.createLoanScheme(request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"),
                    18, 35, Boolean.FALSE, null, 2, 10, Boolean.FALSE, null));
        }

        @Test
        @DisplayName("rejects an effective-from date after the effective-to date")
        void createLoanScheme_invalidDateRange() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(request(
                    LocalDate.now().plusYears(1), LocalDate.now(),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"),
                    18, 35, Boolean.FALSE, null, 2, 10, Boolean.FALSE, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Effective-from");

            verify(loanSchemeRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a minimum loan amount above the maximum")
        void createLoanScheme_invalidAmountRange() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("2000000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"),
                    18, 35, Boolean.FALSE, null, 2, 10, Boolean.FALSE, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Minimum loan amount");
        }

        @Test
        @DisplayName("requires a minimum rate for a FLOATING scheme")
        void createLoanScheme_floatingWithoutMinimumRate() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FLOATING, null, new BigDecimal("11.0"),
                    18, 35, Boolean.FALSE, null, 2, 10, Boolean.FALSE, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("FLOATING");
        }

        @Test
        @DisplayName("rejects a minimum applicant age above the maximum")
        void createLoanScheme_invalidAgeRange() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"),
                    40, 35, Boolean.FALSE, null, 2, 10, Boolean.FALSE, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("applicant age");
        }

        @Test
        @DisplayName("requires a credit score when a co-borrower is mandatory")
        void createLoanScheme_coBorrowerWithoutCreditScore() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"),
                    18, 35, Boolean.TRUE, null, 2, 10, Boolean.FALSE, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("credit score");
        }

        @Test
        @DisplayName("requires foreclosure charges when prepayment is allowed")
        void createLoanScheme_prepaymentWithoutCharges() {
            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(request(
                    LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"),
                    18, 35, Boolean.FALSE, null, 2, 10, Boolean.TRUE, null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Foreclosure charges");
        }

        @Test
        @DisplayName("rejects an unknown document id in the required-documents set")
        void createLoanScheme_unknownDocumentId() {
            Set<UUID> documentIds = Set.of(UUID.randomUUID());

            authenticateAsBank();
            when(bankRepository.findByUserId(bankUser.getId())).thenReturn(Optional.of(TestData.bank()));
            when(loanSchemeRepository.save(any(LoanScheme.class))).thenAnswer(invocation -> {
                LoanScheme saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });
            when(documentTypeRepository.findAllById(documentIds)).thenReturn(List.of());

            CreateLoanSchemeRequestDTO withDocuments = new CreateLoanSchemeRequestDTO(
                    "Scheme", LocalDate.now(), LocalDate.now().plusYears(1),
                    new BigDecimal("50000"), new BigDecimal("1000000"),
                    InterestType.FIXED, null, new BigDecimal("11.0"), DisbursementType.YEARLY,
                    18, 35, Boolean.FALSE, null, documentIds, Set.of(),
                    2, 10, Boolean.FALSE, null, Boolean.TRUE, 6);

            assertThatThrownBy(() -> loanSchemeService.createLoanScheme(withDocuments))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("document ids are invalid");
        }
    }

    @Nested
    @DisplayName("updateLoanScheme")
    class UpdateLoanScheme {

        private LoanScheme scheme;
        private LoanSchemeEligibility eligibility;
        private LoanSchemeRepaymentRule repaymentRule;
        private LoanSchemeMoratorium moratorium;

        @BeforeEach
        void setUp() {
            scheme = TestData.loanScheme(bankUser);

            eligibility = new LoanSchemeEligibility();
            eligibility.setLoanScheme(scheme);
            eligibility.setMinAge(18);
            eligibility.setMaxAge(35);
            eligibility.setCoBorrowerRequired(true);
            eligibility.setMinCreditScore(700);

            repaymentRule = new LoanSchemeRepaymentRule();
            repaymentRule.setLoanScheme(scheme);
            repaymentRule.setMinTenureYears(2);
            repaymentRule.setMaxTenureYears(10);
            repaymentRule.setPrepaymentAllowed(true);
            repaymentRule.setForeclosureCharges(new BigDecimal("2.5"));

            moratorium = new LoanSchemeMoratorium();
            moratorium.setLoanScheme(scheme);
            moratorium.setCoursePeriodIncluded(true);
            moratorium.setAdditionalMonths(6);
        }

        private UpdateLoanSchemeRequestDTO emptyPatch() {
            return new UpdateLoanSchemeRequestDTO(
                    null, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null,
                    null, null, null, null);
        }

        private UpdateLoanSchemeRequestDTO renamePatch(String name) {
            return new UpdateLoanSchemeRequestDTO(
                    name, null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null,
                    null, null, null, SchemeStatus.INACTIVE);
        }

        private void stubChildRows() {
            when(loanSchemeEligibilityRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(eligibility));
            when(loanSchemeRepaymentRuleRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(repaymentRule));
            when(loanSchemeMoratoriumRepository.findByLoanSchemeId(scheme.getId()))
                    .thenReturn(Optional.of(moratorium));
        }

        @Test
        @DisplayName("applies a partial patch and re-saves every affected row")
        void updateLoanScheme_success() {
            authenticateAsBank();
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            stubChildRows();

            loanSchemeService.updateLoanScheme(scheme.getId(), renamePatch("  Renamed Scheme  "));

            assertThat(scheme.getName()).isEqualTo("Renamed Scheme");
            assertThat(scheme.getStatus()).isEqualTo(SchemeStatus.INACTIVE);
            assertThat(scheme.getUpdatedBy()).isSameAs(bankUser);

            verify(loanSchemeRepository).save(scheme);
            verify(loanSchemeEligibilityRepository).save(eligibility);
            verify(loanSchemeRepaymentRuleRepository).save(repaymentRule);
            verify(loanSchemeMoratoriumRepository).save(moratorium);
        }

        @Test
        @DisplayName("leaves everything untouched for an empty patch")
        void updateLoanScheme_emptyPatch() {
            String originalName = scheme.getName();

            authenticateAsBank();
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            stubChildRows();

            loanSchemeService.updateLoanScheme(scheme.getId(), emptyPatch());

            assertThat(scheme.getName()).isEqualTo(originalName);
            assertThat(scheme.getStatus()).isEqualTo(SchemeStatus.ACTIVE);
        }

        @Test
        @DisplayName("clears the credit score when the co-borrower requirement is switched off")
        void updateLoanScheme_coBorrowerSwitchedOff() {
            UpdateLoanSchemeRequestDTO patch = new UpdateLoanSchemeRequestDTO(
                    null, null, null, null, null, null, null, null, null,
                    null, null, Boolean.FALSE, null, null, null, null, null, null,
                    null, null, null, null);

            authenticateAsBank();
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            stubChildRows();

            loanSchemeService.updateLoanScheme(scheme.getId(), patch);

            assertThat(eligibility.isCoBorrowerRequired()).isFalse();
            assertThat(eligibility.getMinCreditScore()).isNull();
        }

        @Test
        @DisplayName("fails for an unknown scheme id")
        void updateLoanScheme_notFound() {
            UUID schemeId = UUID.randomUUID();

            authenticateAsBank();
            when(loanSchemeRepository.findById(schemeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    loanSchemeService.updateLoanScheme(schemeId, emptyPatch()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Loan scheme not found");
        }

        @Test
        @DisplayName("refuses a bank user who did not create the scheme")
        void updateLoanScheme_notTheCreator() {
            LoanScheme foreignScheme = TestData.loanScheme(TestData.user(RoleName.BANK));

            authenticateAsBank();
            when(loanSchemeRepository.findById(foreignScheme.getId()))
                    .thenReturn(Optional.of(foreignScheme));

            assertThatThrownBy(() ->
                    loanSchemeService.updateLoanScheme(foreignScheme.getId(), emptyPatch()))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("not authorized");

            verify(loanSchemeRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a patch that inverts the effective date range")
        void updateLoanScheme_invertedDates() {
            UpdateLoanSchemeRequestDTO patch = new UpdateLoanSchemeRequestDTO(
                    null, LocalDate.now().plusYears(5), LocalDate.now(), null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null, null,
                    null, null, null, null);

            authenticateAsBank();
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));

            assertThatThrownBy(() -> loanSchemeService.updateLoanScheme(scheme.getId(), patch))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Effective-from");
        }

        @Test
        @DisplayName("rejects an empty required-documents set")
        void updateLoanScheme_emptyRequiredDocuments() {
            UpdateLoanSchemeRequestDTO patch = new UpdateLoanSchemeRequestDTO(
                    null, null, null, null, null, null, null, null, null,
                    null, null, null, null, Set.of(), null, null, null, null,
                    null, null, null, null);

            authenticateAsBank();
            when(loanSchemeRepository.findById(scheme.getId())).thenReturn(Optional.of(scheme));
            stubChildRows();

            assertThatThrownBy(() -> loanSchemeService.updateLoanScheme(scheme.getId(), patch))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("At least one required document");
        }
    }
}
