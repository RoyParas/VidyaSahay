package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.CreateBankRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateBankRequest;
import com.vidyasahay.vidyasahay.dto.response.BankDetailedResponse;
import com.vidyasahay.vidyasahay.dto.response.BankSummaryResponse;
import com.vidyasahay.vidyasahay.entity.Bank;
import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.repository.BankRepository;
import com.vidyasahay.vidyasahay.repository.RoleRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BankServiceImpl")
class BankServiceImplTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BankServiceImpl bankService;

    private CreateBankRequest createRequest() {
        return new CreateBankRequest(
                "  Asha  ", "  Rao  ", "  ASHA@BANK.COM  ", " 9812345678 ", "  State Bank of Vidya  ");
    }

    @Nested
    @DisplayName("createBank")
    class CreateBank {

        @Test
        @DisplayName("creates a bank user with a temporary password and links the bank row")
        void createBank_success() {
            Role bankRole = TestData.role(RoleName.BANK);
            UUID userId = UUID.randomUUID();

            when(userRepository.existsByEmailIgnoreCase("asha@bank.com")).thenReturn(false);
            when(bankRepository.existsByNameIgnoreCase("State Bank of Vidya")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(roleRepository.findByName(RoleName.BANK)).thenReturn(Optional.of(bankRole));
            when(passwordEncoder.encode(anyString())).thenReturn("temp-hash");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(userId);
                return user;
            });

            bankService.createBank(createRequest());

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getFirstName()).isEqualTo("Asha");
            assertThat(savedUser.getLastName()).isEqualTo("Rao");
            assertThat(savedUser.getEmail()).isEqualTo("asha@bank.com");
            assertThat(savedUser.getMobile()).isEqualTo("9812345678");
            assertThat(savedUser.getHashedPassword()).isEqualTo("temp-hash");
            assertThat(savedUser.isMustChangePassword()).isTrue();
            assertThat(savedUser.isActive()).isTrue();
            assertThat(savedUser.getRole()).isSameAs(bankRole);

            ArgumentCaptor<Bank> bankCaptor = ArgumentCaptor.forClass(Bank.class);
            verify(bankRepository).save(bankCaptor.capture());

            assertThat(bankCaptor.getValue().getName()).isEqualTo("State Bank of Vidya");
            assertThat(bankCaptor.getValue().getUser().getId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("rejects a duplicate email before touching anything else")
        void createBank_duplicateEmail() {
            when(userRepository.existsByEmailIgnoreCase("asha@bank.com")).thenReturn(true);

            assertThatThrownBy(() -> bankService.createBank(createRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("email");

            verify(userRepository, never()).save(any());
            verify(bankRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a duplicate bank name")
        void createBank_duplicateBankName() {
            when(userRepository.existsByEmailIgnoreCase("asha@bank.com")).thenReturn(false);
            when(bankRepository.existsByNameIgnoreCase("State Bank of Vidya")).thenReturn(true);

            assertThatThrownBy(() -> bankService.createBank(createRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("bank already exists");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a duplicate mobile number")
        void createBank_duplicateMobile() {
            when(userRepository.existsByEmailIgnoreCase("asha@bank.com")).thenReturn(false);
            when(bankRepository.existsByNameIgnoreCase("State Bank of Vidya")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(true);

            assertThatThrownBy(() -> bankService.createBank(createRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("mobile");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("fails when the BANK role row is missing")
        void createBank_missingRole() {
            when(userRepository.existsByEmailIgnoreCase("asha@bank.com")).thenReturn(false);
            when(bankRepository.existsByNameIgnoreCase("State Bank of Vidya")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(roleRepository.findByName(RoleName.BANK)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bankService.createBank(createRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("BANK role");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getAllBanks")
    class GetAllBanks {

        @Test
        @DisplayName("maps every bank to its summary")
        void getAllBanks_success() {
            Bank bank = TestData.bank();

            when(bankRepository.findAllBy()).thenReturn(List.of(bank));

            List<BankSummaryResponse> response = bankService.getAllBanks();

            assertThat(response).hasSize(1);
            assertThat(response.get(0).bankId()).isEqualTo(bank.getId());
            assertThat(response.get(0).bankName()).isEqualTo(bank.getName());
            assertThat(response.get(0).email()).isEqualTo(bank.getUser().getEmail());
            assertThat(response.get(0).active()).isTrue();
        }

        @Test
        @DisplayName("fails when there is not a single bank on the portal")
        void getAllBanks_empty() {
            when(bankRepository.findAllBy()).thenReturn(List.of());

            assertThatThrownBy(() -> bankService.getAllBanks())
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("No banks found");
        }
    }

    @Nested
    @DisplayName("getBankById")
    class GetBankById {

        @Test
        @DisplayName("returns the bank together with its contact user")
        void getBankById_success() {
            Bank bank = TestData.bank();

            when(bankRepository.findOneById(bank.getId())).thenReturn(Optional.of(bank));

            BankDetailedResponse response = bankService.getBankById(bank.getId());

            assertThat(response.bankId()).isEqualTo(bank.getId());
            assertThat(response.userId()).isEqualTo(bank.getUser().getId());
            assertThat(response.bankName()).isEqualTo(bank.getName());
            assertThat(response.contactPersonFirstName()).isEqualTo(bank.getUser().getFirstName());
            assertThat(response.status()).isTrue();
        }

        @Test
        @DisplayName("fails for an unknown bank id")
        void getBankById_notFound() {
            UUID bankId = UUID.randomUUID();

            when(bankRepository.findOneById(bankId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bankService.getBankById(bankId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Bank not found");
        }
    }

    @Nested
    @DisplayName("updateBank")
    class UpdateBank {

        private final UpdateBankRequest request = new UpdateBankRequest(
                "  Meera  ", "  Iyer  ", "  MEERA@BANK.COM  ", " 9898989898 ");

        @Test
        @DisplayName("updates the linked contact user")
        void updateBank_success() {
            Bank bank = TestData.bank();
            User user = bank.getUser();

            when(bankRepository.findOneById(bank.getId())).thenReturn(Optional.of(bank));
            when(userRepository.existsByEmailIgnoreCaseAndIdNot("meera@bank.com", user.getId()))
                    .thenReturn(false);
            when(userRepository.existsByMobileAndIdNot("9898989898", user.getId()))
                    .thenReturn(false);

            bankService.updateBank(bank.getId(), request);

            assertThat(user.getFirstName()).isEqualTo("Meera");
            assertThat(user.getLastName()).isEqualTo("Iyer");
            assertThat(user.getEmail()).isEqualTo("meera@bank.com");
            assertThat(user.getMobile()).isEqualTo("9898989898");

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("fails for an unknown bank id")
        void updateBank_notFound() {
            UUID bankId = UUID.randomUUID();

            when(bankRepository.findOneById(bankId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bankService.updateBank(bankId, request))
                    .isInstanceOf(BusinessException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects an email already used by another account")
        void updateBank_duplicateEmail() {
            Bank bank = TestData.bank();

            when(bankRepository.findOneById(bank.getId())).thenReturn(Optional.of(bank));
            when(userRepository.existsByEmailIgnoreCaseAndIdNot("meera@bank.com", bank.getUser().getId()))
                    .thenReturn(true);

            assertThatThrownBy(() -> bankService.updateBank(bank.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("email");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a mobile number already used by another account")
        void updateBank_duplicateMobile() {
            Bank bank = TestData.bank();

            when(bankRepository.findOneById(bank.getId())).thenReturn(Optional.of(bank));
            when(userRepository.existsByEmailIgnoreCaseAndIdNot("meera@bank.com", bank.getUser().getId()))
                    .thenReturn(false);
            when(userRepository.existsByMobileAndIdNot("9898989898", bank.getUser().getId()))
                    .thenReturn(true);

            assertThatThrownBy(() -> bankService.updateBank(bank.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("mobile");

            verify(userRepository, never()).save(any());
        }
    }
}
