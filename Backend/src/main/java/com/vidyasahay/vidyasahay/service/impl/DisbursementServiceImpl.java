package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.CreateDisbursementRequest;
import com.vidyasahay.vidyasahay.dto.response.DisbursementDetailResponse;
import com.vidyasahay.vidyasahay.dto.response.DisbursementSummaryResponse;
import com.vidyasahay.vidyasahay.service.DisbursementService;
import com.vidyasahay.vidyasahay.entity.Application;
import com.vidyasahay.vidyasahay.entity.Disbursement;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.ApplicationType;
import com.vidyasahay.vidyasahay.enums.DisbursementStatus;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.repository.ApplicationRepository;
import com.vidyasahay.vidyasahay.repository.DisbursementRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DisbursementServiceImpl
        implements DisbursementService {

    private final DisbursementRepository disbursementRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public DisbursementServiceImpl(
            DisbursementRepository disbursementRepository,
            ApplicationRepository applicationRepository,
            UserRepository userRepository
    ) {
        this.disbursementRepository = disbursementRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisbursementSummaryResponse> getMyDisbursements(
            CustomUserPrincipal principal
    ) {
        List<Disbursement> disbursements;

        if (principal.getRole() == RoleName.BANK) {
            disbursements = disbursementRepository
                    .findAllByApplicationLoanSchemeCreatedByIdOrderByDisbursementDateDesc(
                            principal.getUserId()
                    );
        } else if (principal.getRole() == RoleName.GOVERNMENT) {
            disbursements = disbursementRepository
                    .findAllByApplicationScholarshipSchemeCreatedByIdOrderByDisbursementDateDesc(
                            principal.getUserId()
                    );
        } else {
            throw new RuntimeException(
                    "Only bank and government users can access disbursements"
            );
        }

        return disbursements.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DisbursementDetailResponse getDisbursementById(
            UUID disbursementId,
            CustomUserPrincipal principal
    ) {
        Disbursement disbursement;

        if (principal.getRole() == RoleName.BANK) {
            disbursement = disbursementRepository
                    .findByIdAndApplicationLoanSchemeCreatedById(
                            disbursementId,
                            principal.getUserId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Disbursement not found"
                            )
                    );
        } else if (principal.getRole() == RoleName.GOVERNMENT) {
            disbursement = disbursementRepository
                    .findByIdAndApplicationScholarshipSchemeCreatedById(
                            disbursementId,
                            principal.getUserId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Disbursement not found"
                            )
                    );
        } else {
            throw new RuntimeException(
                    "Only bank and government users can access disbursements"
            );
        }

        return toDetailResponse(disbursement);
    }

    @Override
    @Transactional
    public DisbursementDetailResponse createDisbursement(
            CreateDisbursementRequest request,
            CustomUserPrincipal principal
    ) {
        Application application = findAccessibleApplication(
                request.applicationId(),
                principal
        );

        User disbursedByUser = userRepository
                .findById(principal.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );

        DisbursementStatus status;

        try {
            status = DisbursementStatus.valueOf(
                    request.status().trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException(
                    "Invalid disbursement status"
            );
        }

        Disbursement disbursement = new Disbursement();

        disbursement.setApplication(application);
        disbursement.setApplicationType(
                application.getApplicationType()
        );
        disbursement.setStudent(
                application.getStudent()
        );
        disbursement.setDisbursedByUser(
                disbursedByUser
        );
        disbursement.setAmount(
                request.amount()
        );
        disbursement.setDisbursementDate(
                request.disbursementDate()
        );
        disbursement.setStatus(status);
        disbursement.setRemark(
                request.remark()
        );

        Disbursement savedDisbursement =
                disbursementRepository.save(disbursement);

        return toDetailResponse(savedDisbursement);
    }

    private Application findAccessibleApplication(
            UUID applicationId,
            CustomUserPrincipal principal
    ) {
        if (principal.getRole() == RoleName.BANK) {
            Application application = applicationRepository
                    .findByIdAndLoanSchemeCreatedById(
                            applicationId,
                            principal.getUserId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Loan application not found"
                            )
                    );

            if (application.getApplicationType()
                    != ApplicationType.LOAN) {
                throw new RuntimeException(
                        "Bank users can create disbursements only for loan applications"
                );
            }

            return application;
        }

        if (principal.getRole() == RoleName.GOVERNMENT) {
            Application application = applicationRepository
                    .findByIdAndScholarshipSchemeCreatedById(
                            applicationId,
                            principal.getUserId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Scholarship application not found"
                            )
                    );

            if (application.getApplicationType()
                    != ApplicationType.SCHOLARSHIP) {
                throw new RuntimeException(
                        "Government users can create disbursements only for scholarship applications"
                );
            }

            return application;
        }

        throw new RuntimeException(
                "Only bank and government users can create disbursements"
        );
    }

    private DisbursementSummaryResponse toSummaryResponse(
            Disbursement disbursement
    ) {
        return new DisbursementSummaryResponse(
                disbursement.getId(),
                disbursement.getApplication().getId(),
                disbursement.getApplicationType().name(),
                disbursement.getStudent().getId(),
                disbursement.getStudent()
                        .getUser()
                        .getFirstName(),
                disbursement.getStudent()
                        .getUser()
                        .getLastName(),
                disbursement.getAmount(),
                disbursement.getDisbursementDate(),
                disbursement.getStatus().name(),
                disbursement.getRemark()
        );
    }

    private DisbursementDetailResponse toDetailResponse(
            Disbursement disbursement
    ) {
        return new DisbursementDetailResponse(
                disbursement.getId(),
                disbursement.getApplication().getId(),
                disbursement.getApplicationType().name(),
                disbursement.getStudent().getId(),
                disbursement.getStudent()
                        .getUser()
                        .getFirstName(),
                disbursement.getStudent()
                        .getUser()
                        .getLastName(),
                disbursement.getDisbursedByUser().getId(),
                disbursement.getDisbursedByUser()
                        .getFirstName(),
                disbursement.getDisbursedByUser()
                        .getLastName(),
                disbursement.getAmount(),
                disbursement.getDisbursementDate(),
                disbursement.getStatus().name(),
                disbursement.getRemark()
        );
    }
}