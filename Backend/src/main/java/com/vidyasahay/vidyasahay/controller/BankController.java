package com.vidyasahay.vidyasahay.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vidyasahay.vidyasahay.dto.response.BankDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.BankSummaryResponse;
import com.vidyasahay.vidyasahay.dto.request.CreateBankRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateBankRequest;
import com.vidyasahay.vidyasahay.service.BankService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bank")
@PreAuthorize("hasRole('ADMIN')")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    public ResponseEntity<Void> createBank(@Valid @RequestBody CreateBankRequest request) {
        bankService.createBank(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<BankSummaryResponse>> getAllBanks() {
        return ResponseEntity.ok(bankService.getAllBanks());
    }

    @GetMapping("/{bankId}")
    public ResponseEntity<BankDetailedResponse> getBankById(@PathVariable UUID bankId) {
        return ResponseEntity.ok(bankService.getBankById(bankId));
    }

    @PatchMapping("/{bankId}")
    public ResponseEntity<Void> updateBank(@PathVariable UUID bankId, @Valid @RequestBody UpdateBankRequest request) {
        bankService.updateBank(bankId, request);
        return ResponseEntity.ok().build();
    }
}