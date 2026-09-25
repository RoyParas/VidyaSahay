package com.vidyasahay.vidyasahay.repository;

import com.vidyasahay.vidyasahay.entity.Disbursement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisbursementRepository
        extends JpaRepository<Disbursement, UUID> {

    /*
     * BANK:
     * Gets disbursements for loan schemes created by
     * the authenticated bank user.
     */
    @EntityGraph(attributePaths = {
            "application",
            "application.loanScheme",
            "student",
            "student.user",
            "disbursedByUser"
    })
    List<Disbursement>
    findAllByApplicationLoanSchemeCreatedByIdOrderByDisbursementDateDesc(
            UUID userId
    );

    /*
     * GOVERNMENT:
     * Gets disbursements for scholarship schemes created by
     * the authenticated government user.
     */
    @EntityGraph(attributePaths = {
            "application",
            "application.scholarshipScheme",
            "student",
            "student.user",
            "disbursedByUser"
    })
    List<Disbursement>
    findAllByApplicationScholarshipSchemeCreatedByIdOrderByDisbursementDateDesc(
            UUID userId
    );

    /*
     * BANK:
     * Gets one disbursement only when its loan scheme
     * was created by the authenticated user.
     */
    @EntityGraph(attributePaths = {
            "application",
            "application.loanScheme",
            "student",
            "student.user",
            "disbursedByUser"
    })
    Optional<Disbursement>
    findByIdAndApplicationLoanSchemeCreatedById(
            UUID disbursementId,
            UUID userId
    );

    /*
     * GOVERNMENT:
     * Gets one disbursement only when its scholarship scheme
     * was created by the authenticated user.
     */
    @EntityGraph(attributePaths = {
            "application",
            "application.scholarshipScheme",
            "student",
            "student.user",
            "disbursedByUser"
    })
    Optional<Disbursement>
    findByIdAndApplicationScholarshipSchemeCreatedById(
            UUID disbursementId,
            UUID userId
    );
}