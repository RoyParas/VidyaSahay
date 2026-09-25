package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.CreateBankRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateBankRequest;
import com.vidyasahay.vidyasahay.dto.response.BankDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.BankSummaryResponse;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.service.BankService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankController")
class BankControllerTest {

    @Mock
    private BankService bankService;

    @InjectMocks
    private BankController bankController;

    private CreateBankRequest createRequest() {
        return new CreateBankRequest("Asha", "Rao", "asha@bank.com", "9812345678", "State Bank of Vidya");
    }

    private UpdateBankRequest updateRequest() {
        return new UpdateBankRequest("Meera", "Iyer", "meera@bank.com", "9898989898");
    }

    @Test
    @DisplayName("POST /api/admin/banks returns 201 with an empty body")
    void createBank_success() {
        CreateBankRequest request = createRequest();

        ResponseEntity<Void> response = bankController.createBank(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();

        verify(bankService).createBank(request);
    }

    @Test
    @DisplayName("POST /api/admin/banks propagates a duplicate-account failure")
    void createBank_duplicate() {
        CreateBankRequest request = createRequest();

        doThrow(new BusinessException("An account already exists with this email"))
                .when(bankService).createBank(request);

        assertThatThrownBy(() -> bankController.createBank(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("GET /api/admin/banks returns 200 with every bank summary")
    void getAllBanks_success() {
        BankSummaryResponse summary = new BankSummaryResponse(
                UUID.randomUUID(), "State Bank of Vidya", "Asha", "Rao",
                "asha@bank.com", "9812345678", true);

        when(bankService.getAllBanks()).thenReturn(List.of(summary));

        ResponseEntity<List<BankSummaryResponse>> response = bankController.getAllBanks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(summary);
    }

    @Test
    @DisplayName("GET /api/admin/banks/{id} returns 200 with the bank detail")
    void getBankById_success() {
        UUID bankId = UUID.randomUUID();
        BankDetailedResponse detail = new BankDetailedResponse(
                UUID.randomUUID(), bankId, "State Bank of Vidya", "Asha", "Rao",
                "asha@bank.com", "9812345678", true);

        when(bankService.getBankById(bankId)).thenReturn(detail);

        ResponseEntity<BankDetailedResponse> response = bankController.getBankById(bankId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(detail);
    }

    @Test
    @DisplayName("GET /api/admin/banks/{id} propagates the not-found failure")
    void getBankById_notFound() {
        UUID bankId = UUID.randomUUID();

        when(bankService.getBankById(bankId))
                .thenThrow(new BusinessException("Bank not found with ID: " + bankId));

        assertThatThrownBy(() -> bankController.getBankById(bankId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("PATCH /api/admin/banks/{id} returns 200 and delegates both arguments")
    void updateBank_success() {
        UUID bankId = UUID.randomUUID();
        UpdateBankRequest request = updateRequest();

        ResponseEntity<Void> response = bankController.updateBank(bankId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(bankService).updateBank(bankId, request);
    }
}
