package com.vidyasahay.vidyasahay.service;

import java.util.List;
import java.util.UUID;

import com.vidyasahay.vidyasahay.dto.request.loanScheme.CreateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.LoanEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.UpdateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeSummaryResponseDTO;

public interface LoanSchemeService {	
	List<LoanSchemeSummaryResponseDTO> getAllLoanSchemes();
	LoanSchemeDetailedResponseDTO getLoanSchemeById(UUID loanSchemeId);
	List<LoanSchemeSummaryResponseDTO> getEligibleLoanSchemes(LoanEligibilityRequestDTO request);
	List<LoanSchemeSummaryResponseDTO> getLoanSchemesCreatedByMe();
	UUID createLoanScheme(CreateLoanSchemeRequestDTO request);
	void updateLoanScheme(UUID loanSchemeId, UpdateLoanSchemeRequestDTO request);
}
