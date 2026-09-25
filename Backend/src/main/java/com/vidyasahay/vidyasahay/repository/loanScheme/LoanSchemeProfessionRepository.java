package com.vidyasahay.vidyasahay.repository.loanScheme;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vidyasahay.vidyasahay.entity.LoanSchemeProfession;

@Repository
public interface LoanSchemeProfessionRepository extends JpaRepository<LoanSchemeProfession, UUID> {

	List<LoanSchemeProfession> findByProfessionId(UUID professionId);

	List<LoanSchemeProfession> findByLoanSchemeId(UUID loanSchemeId);

	void deleteByLoanSchemeId(UUID loanSchemeId);

}