package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.response.BankDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.BankSummaryResponse;
import com.vidyasahay.vidyasahay.dto.request.CreateBankRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateBankRequest;
import com.vidyasahay.vidyasahay.entity.Bank;
import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.repository.BankRepository;
import com.vidyasahay.vidyasahay.repository.RoleRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.BankService;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BankServiceImpl implements BankService {

    private static final String TEMPORARY_PASSWORD = "Abc@1234";

    private final BankRepository bankRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public BankServiceImpl(
            BankRepository bankRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createBank(CreateBankRequest request) {

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        
        String normalizedMobile = request.mobile().trim();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessException(
                    "An account already exists with this email"
            );
        }
        
        String normalizedBankName = request.bankName().trim();

        if (bankRepository.existsByNameIgnoreCase(normalizedBankName)) {
            throw new BusinessException(
                    "A bank already exists with this name"
            );
        }

        if (userRepository.existsByMobile(normalizedMobile)) {
            throw new BusinessException(
                    "An account already exists with this mobile number"
            );
        }

        Role bankRole = roleRepository
                .findByName(RoleName.BANK)
                .orElseThrow(() -> new BusinessException(
                        "BANK role is not configured in the roles table"
                ));

        User user = new User();

        user.setRole(bankRole);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(normalizedEmail);
        user.setMobile(normalizedMobile);

        user.setHashedPassword(
                passwordEncoder.encode(TEMPORARY_PASSWORD)
        );

        user.setMustChangePassword(true);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Bank bank = new Bank();

        bank.setName(normalizedBankName);
        bank.setUser(savedUser);

        bankRepository.save(bank);
    }

    @Override
    @Transactional
    public List<BankSummaryResponse> getAllBanks() {

        List<Bank> banks = bankRepository.findAllBy();

        if (banks.isEmpty()) {
            throw new BusinessException(
                    "No banks found"
            );
        }

        return banks.stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public BankDetailedResponse getBankById(UUID bankId) {

        Bank bank = findBankById(bankId);

        User user = bank.getUser();

        return new BankDetailedResponse(
                user.getId(),
                bank.getId(),
                bank.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                user.isActive()  
        );
    }

    @Override
    @Transactional
    public void updateBank(
            UUID bankId,
            UpdateBankRequest request
    ) {

        Bank bank = findBankById(bankId);

        User user = bank.getUser();

        String normalizedEmail =
                request.email().trim().toLowerCase(Locale.ROOT);

        String normalizedMobile = request.mobile().trim();

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(
                normalizedEmail,
                user.getId()
        )) {
            throw new BusinessException(
                    "Another account already exists with this email"
            );
        }

        if (userRepository.existsByMobileAndIdNot(
                normalizedMobile,
                user.getId()
        )) {
            throw new BusinessException(
                    "Another account already exists with this mobile number"
            );
        }

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(normalizedEmail);
        user.setMobile(normalizedMobile);
        if (request.status() != null) {
            user.setActive(request.status());
        }

        userRepository.save(user);
    }

    private Bank findBankById(UUID bankId) {

        return bankRepository
                .findOneById(bankId)
                .orElseThrow(() -> new BusinessException(
                        "Bank not found with ID: " + bankId
                ));
    }

    private BankSummaryResponse mapToSummaryResponse(Bank bank) {

        User user = bank.getUser();

        return new BankSummaryResponse(
                bank.getId(),
                bank.getName(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                user.isActive()
        );
    }
}
