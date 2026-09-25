package com.vidyasahay.vidyasahay.repository.loanScheme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanSchemeRequiredDocument;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanSchemeRequiredDocumentRepository extends JpaRepository<LoanSchemeRequiredDocument, UUID> {

    List<LoanSchemeRequiredDocument> findByLoanSchemeId(UUID loanSchemeId);

    void deleteByLoanSchemeId(UUID loanSchemeId);
    
    List<LoanSchemeRequiredDocument>
    findByLoanSchemeIdOrderByDocumentTypeNameAsc(
            UUID loanSchemeId
    );
}