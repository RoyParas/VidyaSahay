package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.dto.response.BankDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.BankSummaryResponse;
import com.vidyasahay.vidyasahay.dto.request.CreateBankRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateBankRequest;

import java.util.List;
import java.util.UUID;

public interface BankService {

    void createBank(CreateBankRequest request);

    List<BankSummaryResponse> getAllBanks();

    BankDetailedResponse getBankById(UUID bankId);

    void updateBank(UUID bankId, UpdateBankRequest request);
}