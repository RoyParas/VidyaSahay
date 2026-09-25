package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.CreateDisbursementRequest;
import com.vidyasahay.vidyasahay.dto.response.DisbursementDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.DisbursementSummaryResponse;
import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.Disbursement;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.DisbursementStatus;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.repository.ApplicationRepository;
import com.vidyasahay.vidyasahay.repository.DisbursementRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
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
@DisplayName("DisbursementServiceImpl")
class DisbursementServiceImplTest {

    @Mock
    private DisbursementRepository disbursementRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DisbursementServiceImpl disbursementService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = TestData.student();
    }

    @Nested
    @DisplayName("getMyDisbursements")
    class GetMyDisbursements {

        @Test
        @DisplayName("reads loan disbursements for a bank user")
        void getMyDisbursements_bank() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Application application = TestData.application(student, ApplicationType.LOAN);
            Disbursement disbursement =
                    TestData.disbursement(application, TestData.user(RoleName.BANK));

            when(disbursementRepository
                    .findAllByApplicationLoanSchemeCreatedByIdOrderByDisbursementDateDesc(
                            principal.getUserId()))
                    .thenReturn(List.of(disbursement));

            List<DisbursementSummaryResponse> response =
                    disbursementService.getMyDisbursements(principal);

            assertThat(response).hasSize(1);
            assertThat(response.get(0).id()).isEqualTo(disbursement.getId());
            assertThat(response.get(0).applicationType()).isEqualTo("LOAN");
            assertThat(response.get(0).status()).isEqualTo("COMPLETED");
            assertThat(response.get(0).studentId()).isEqualTo(student.getId());
        }

        @Test
        @DisplayName("reads scholarship disbursements for a government user")
        void getMyDisbursements_government() {
            CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
            Application application = TestData.application(student, ApplicationType.SCHOLARSHIP);
            Disbursement disbursement =
                    TestData.disbursement(application, TestData.user(RoleName.GOVERNMENT));

            when(disbursementRepository
                    .findAllByApplicationScholarshipSchemeCreatedByIdOrderByDisbursementDateDesc(
                            principal.getUserId()))
                    .thenReturn(List.of(disbursement));

            assertThat(disbursementService.getMyDisbursements(principal))
                    .singleElement()
                    .extracting(DisbursementSummaryResponse::applicationType)
                    .isEqualTo("SCHOLARSHIP");
        }

        @Test
        @DisplayName("refuses any other role")
        void getMyDisbursements_otherRole() {
            CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);

            assertThatThrownBy(() -> disbursementService.getMyDisbursements(principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Only bank and government");
        }
    }

    @Nested
    @DisplayName("getDisbursementById")
    class GetDisbursementById {

        @Test
        @DisplayName("returns a disbursement owned by the bank user's loan scheme")
        void getDisbursementById_bank() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Application application = TestData.application(student, ApplicationType.LOAN);
            User disbursedBy = TestData.user(RoleName.BANK);
            Disbursement disbursement = TestData.disbursement(application, disbursedBy);

            when(disbursementRepository.findByIdAndApplicationLoanSchemeCreatedById(
                    disbursement.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(disbursement));

            DisbursementDetailResponse response =
                    disbursementService.getDisbursementById(disbursement.getId(), principal);

            assertThat(response.id()).isEqualTo(disbursement.getId());
            assertThat(response.applicationId()).isEqualTo(application.getId());
            assertThat(response.disbursedByUserId()).isEqualTo(disbursedBy.getId());
            assertThat(response.amount()).isEqualByComparingTo("125000.00");
        }

        @Test
        @DisplayName("fails when the disbursement is not owned by the bank user")
        void getDisbursementById_bankNotOwned() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            UUID disbursementId = UUID.randomUUID();

            when(disbursementRepository.findByIdAndApplicationLoanSchemeCreatedById(
                    disbursementId, principal.getUserId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    disbursementService.getDisbursementById(disbursementId, principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Disbursement not found");
        }

        @Test
        @DisplayName("returns a disbursement owned by the government user's scholarship scheme")
        void getDisbursementById_government() {
            CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
            Application application = TestData.application(student, ApplicationType.SCHOLARSHIP);
            Disbursement disbursement =
                    TestData.disbursement(application, TestData.user(RoleName.GOVERNMENT));

            when(disbursementRepository.findByIdAndApplicationScholarshipSchemeCreatedById(
                    disbursement.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(disbursement));

            assertThat(disbursementService.getDisbursementById(disbursement.getId(), principal)
                    .applicationType()).isEqualTo("SCHOLARSHIP");
        }

        @Test
        @DisplayName("refuses any other role")
        void getDisbursementById_otherRole() {
            CustomUserPrincipal principal = TestData.principal(RoleName.ADMIN);

            assertThatThrownBy(() ->
                    disbursementService.getDisbursementById(UUID.randomUUID(), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Only bank and government");
        }
    }

    @Nested
    @DisplayName("createDisbursement")
    class CreateDisbursement {

        private CreateDisbursementRequest request(UUID applicationId, String status) {
            return new CreateDisbursementRequest(
                    applicationId,
                    new BigDecimal("75000.00"),
                    LocalDate.now(),
                    status,
                    "First tranche");
        }

        @Test
        @DisplayName("creates a loan disbursement for a bank user and parses the status")
        void createDisbursement_bank() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Application application = TestData.application(student, ApplicationType.LOAN);
            User disbursedBy = TestData.user(RoleName.BANK);

            when(applicationRepository.findByIdAndLoanSchemeCreatedById(
                    application.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(application));
            when(userRepository.findById(principal.getUserId()))
                    .thenReturn(Optional.of(disbursedBy));
            when(disbursementRepository.save(any(Disbursement.class)))
                    .thenAnswer(invocation -> {
                        Disbursement saved = invocation.getArgument(0);
                        saved.setId(UUID.randomUUID());
                        return saved;
                    });

            DisbursementDetailResponse response = disbursementService.createDisbursement(
                    request(application.getId(), "  completed  "), principal);

            assertThat(response.status()).isEqualTo("COMPLETED");
            assertThat(response.applicationType()).isEqualTo("LOAN");
            assertThat(response.amount()).isEqualByComparingTo("75000.00");

            ArgumentCaptor<Disbursement> captor = ArgumentCaptor.forClass(Disbursement.class);
            verify(disbursementRepository).save(captor.capture());

            assertThat(captor.getValue().getStatus()).isEqualTo(DisbursementStatus.COMPLETED);
            assertThat(captor.getValue().getStudent()).isSameAs(student);
            assertThat(captor.getValue().getDisbursedByUser()).isSameAs(disbursedBy);
        }

        @Test
        @DisplayName("creates a scholarship disbursement for a government user")
        void createDisbursement_government() {
            CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
            Application application = TestData.application(student, ApplicationType.SCHOLARSHIP);

            when(applicationRepository.findByIdAndScholarshipSchemeCreatedById(
                    application.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(application));
            when(userRepository.findById(principal.getUserId()))
                    .thenReturn(Optional.of(TestData.user(RoleName.GOVERNMENT)));
            when(disbursementRepository.save(any(Disbursement.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            assertThat(disbursementService.createDisbursement(
                    request(application.getId(), "PENDING"), principal).status())
                    .isEqualTo("PENDING");
        }

        @Test
        @DisplayName("fails when the loan application is not owned by the bank user")
        void createDisbursement_applicationNotFound() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            UUID applicationId = UUID.randomUUID();

            when(applicationRepository.findByIdAndLoanSchemeCreatedById(
                    applicationId, principal.getUserId()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> disbursementService.createDisbursement(
                    request(applicationId, "COMPLETED"), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Loan application not found");

            verify(disbursementRepository, never()).save(any());
        }

        @Test
        @DisplayName("stops a bank user from disbursing against a scholarship application")
        void createDisbursement_bankOnScholarshipApplication() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Application application = TestData.application(student, ApplicationType.SCHOLARSHIP);

            when(applicationRepository.findByIdAndLoanSchemeCreatedById(
                    application.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(application));

            assertThatThrownBy(() -> disbursementService.createDisbursement(
                    request(application.getId(), "COMPLETED"), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("only for loan applications");

            verify(disbursementRepository, never()).save(any());
        }

        @Test
        @DisplayName("stops a government user from disbursing against a loan application")
        void createDisbursement_governmentOnLoanApplication() {
            CustomUserPrincipal principal = TestData.principal(RoleName.GOVERNMENT);
            Application application = TestData.application(student, ApplicationType.LOAN);

            when(applicationRepository.findByIdAndScholarshipSchemeCreatedById(
                    application.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(application));

            assertThatThrownBy(() -> disbursementService.createDisbursement(
                    request(application.getId(), "COMPLETED"), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("only for scholarship applications");
        }

        @Test
        @DisplayName("refuses any role other than bank and government")
        void createDisbursement_otherRole() {
            CustomUserPrincipal principal = TestData.principal(RoleName.INSTITUTE);

            assertThatThrownBy(() -> disbursementService.createDisbursement(
                    request(UUID.randomUUID(), "COMPLETED"), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Only bank and government");
        }

        @Test
        @DisplayName("fails when the authenticated user row is missing")
        void createDisbursement_userNotFound() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Application application = TestData.application(student, ApplicationType.LOAN);

            when(applicationRepository.findByIdAndLoanSchemeCreatedById(
                    application.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(application));
            when(userRepository.findById(principal.getUserId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> disbursementService.createDisbursement(
                    request(application.getId(), "COMPLETED"), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Authenticated user not found");
        }

        @Test
        @DisplayName("rejects a status string that is not a DisbursementStatus")
        void createDisbursement_invalidStatus() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Application application = TestData.application(student, ApplicationType.LOAN);

            when(applicationRepository.findByIdAndLoanSchemeCreatedById(
                    application.getId(), principal.getUserId()))
                    .thenReturn(Optional.of(application));
            when(userRepository.findById(principal.getUserId()))
                    .thenReturn(Optional.of(TestData.user(RoleName.BANK)));

            assertThatThrownBy(() -> disbursementService.createDisbursement(
                    request(application.getId(), "SETTLED"), principal))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid disbursement status");

            verify(disbursementRepository, never()).save(any());
        }
    }
}
