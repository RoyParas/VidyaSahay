package com.vidyasahay.vidyasahay.repository.loanScheme;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanSchemeMoratorium;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanSchemeMoratoriumRepository extends JpaRepository<LoanSchemeMoratorium, UUID> {

    Optional<LoanSchemeMoratorium> findByLoanSchemeId(UUID loanSchemeId);
    
}