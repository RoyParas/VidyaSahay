package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.ChangePasswordRequest;
import com.vidyasahay.vidyasahay.dto.request.LoginRequest;
import com.vidyasahay.vidyasahay.dto.request.StudentRegistrationRequest;
import com.vidyasahay.vidyasahay.dto.response.ChangePasswordResponse;
import com.vidyasahay.vidyasahay.dto.response.LoginResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentRegistrationResponse;
import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.jwt.JwtTokenProvider;
import com.vidyasahay.vidyasahay.repository.RoleRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthServiceImpl}. Every collaborator is a Mockito mock,
 * so no Spring context and no database are involved.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("returns the token, the expiry in seconds and the authenticated user")
        void login_success() {
            CustomUserPrincipal principal = TestData.principal(RoleName.STUDENT);
            Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(principal);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(tokenProvider.generateToken(principal)).thenReturn("jwt-token");
            when(tokenProvider.getExpirationMs()).thenReturn(3_600_000L);

            LoginResponse response = authService.login(
                    new LoginRequest("jane@example.com", "Passw0rd!"));

            assertThat(response.accessToken()).isEqualTo("jwt-token");
            assertThat(response.expiresIn()).isEqualTo(3_600L);
            assertThat(response.user().userId()).isEqualTo(principal.getUserId());
            assertThat(response.user().email()).isEqualTo("jane@example.com");
            assertThat(response.user().role()).isEqualTo(RoleName.STUDENT);
            assertThat(response.user().mustChangePassword()).isFalse();
        }

        @Test
        @DisplayName("trims the submitted email before handing it to Spring Security")
        void login_trimsEmail() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);
            Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

            when(authentication.getPrincipal()).thenReturn(principal);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(tokenProvider.generateToken(principal)).thenReturn("jwt-token");
            when(tokenProvider.getExpirationMs()).thenReturn(1_000L);

            authService.login(new LoginRequest("   jane@example.com   ", "Passw0rd!"));

            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

            verify(authenticationManager).authenticate(captor.capture());

            assertThat(captor.getValue().getPrincipal()).isEqualTo("jane@example.com");
            assertThat(captor.getValue().getCredentials()).isEqualTo("Passw0rd!");
        }

        @Test
        @DisplayName("lets an authentication failure bubble up and never mints a token")
        void login_badCredentials() {
            when(authenticationManager.authenticate(any()))
                    .thenThrow(new BadCredentialsException("Invalid email or password"));

            assertThatThrownBy(() ->
                    authService.login(new LoginRequest("jane@example.com", "wrong")))
                    .isInstanceOf(BadCredentialsException.class);

            verify(tokenProvider, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("registerStudent")
    class RegisterStudent {

        private StudentRegistrationRequest request() {
            return new StudentRegistrationRequest(
                    "  Jane  ", "  Doe  ", "  JANE@Example.COM  ", " 9876543210 ", "Passw0rd!");
        }

        @Test
        @DisplayName("normalises the input, hashes the password and saves an active student")
        void registerStudent_success() {
            Role studentRole = TestData.role(RoleName.STUDENT);
            UUID generatedId = UUID.randomUUID();

            when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
            when(userRepository.existsByMobile("9876543210")).thenReturn(false);
            when(roleRepository.findByName(RoleName.STUDENT)).thenReturn(Optional.of(studentRole));
            when(passwordEncoder.encode("Passw0rd!")).thenReturn("hashed-password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(generatedId);
                return user;
            });

            StudentRegistrationResponse response = authService.registerStudent(request());

            assertThat(response.userId()).isEqualTo(generatedId);
            assertThat(response.firstName()).isEqualTo("Jane");
            assertThat(response.lastName()).isEqualTo("Doe");
            assertThat(response.email()).isEqualTo("jane@example.com");
            assertThat(response.mobile()).isEqualTo("9876543210");
            assertThat(response.role()).isEqualTo(RoleName.STUDENT);
            assertThat(response.mustCompleteProfile()).isTrue();
            assertThat(response.message()).isNotBlank();

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());

            User saved = captor.getValue();
            assertThat(saved.getEmail()).isEqualTo("jane@example.com");
            assertThat(saved.getHashedPassword()).isEqualTo("hashed-password");
            assertThat(saved.isActive()).isTrue();
            assertThat(saved.isMustChangePassword()).isFalse();
            assertThat(saved.getRole()).isSameAs(studentRole);
        }

        @Test
        @DisplayName("rejects an email that is already registered")
        void registerStudent_duplicateEmail() {
            when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerStudent(request()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("email");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a mobile number that is already registered")
        void registerStudent_duplicateMobile() {
            when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
            when(userRepository.existsByMobile("9876543210")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerStudent(request()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("mobile");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("fails when the STUDENT role row is missing")
        void registerStudent_missingRole() {
            when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
            when(userRepository.existsByMobile("9876543210")).thenReturn(false);
            when(roleRepository.findByName(RoleName.STUDENT)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.registerStudent(request()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("STUDENT role");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        private final ChangePasswordRequest request =
                new ChangePasswordRequest("Old@1234", "New@12345");

        private User bankUserRequiringChange() {
            User user = TestData.user(RoleName.BANK);
            user.setMustChangePassword(true);
            user.setHashedPassword("old-hash");
            return user;
        }

        @Test
        @DisplayName("stores the new hash and clears the mandatory-change flag")
        void changePassword_success() {
            User user = bankUserRequiringChange();
            CustomUserPrincipal principal = TestData.principal(user.getId(), RoleName.BANK);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Old@1234", "old-hash")).thenReturn(true);
            when(passwordEncoder.matches("New@12345", "old-hash")).thenReturn(false);
            when(passwordEncoder.encode("New@12345")).thenReturn("new-hash");

            ChangePasswordResponse response = authService.changePassword(request, principal);

            assertThat(response.mustChangePassword()).isFalse();
            assertThat(response.message()).isNotBlank();
            assertThat(user.getHashedPassword()).isEqualTo("new-hash");
            assertThat(user.isMustChangePassword()).isFalse();

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("fails when the authenticated user no longer exists")
        void changePassword_userNotFound() {
            CustomUserPrincipal principal = TestData.principal(RoleName.BANK);

            when(userRepository.findById(principal.getUserId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.changePassword(request, principal))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("is refused for roles other than BANK and INSTITUTE")
        void changePassword_forbiddenRole() {
            User user = TestData.user(RoleName.STUDENT);
            CustomUserPrincipal principal = TestData.principal(user.getId(), RoleName.STUDENT);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.changePassword(request, principal))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("bank and institute");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("is refused when no mandatory change is pending")
        void changePassword_notPending() {
            User user = TestData.user(RoleName.INSTITUTE);
            user.setMustChangePassword(false);
            CustomUserPrincipal principal = TestData.principal(user.getId(), RoleName.INSTITUTE);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.changePassword(request, principal))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Mandatory password change");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("is refused when the current password does not match")
        void changePassword_wrongCurrentPassword() {
            User user = bankUserRequiringChange();
            CustomUserPrincipal principal = TestData.principal(user.getId(), RoleName.BANK);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Old@1234", "old-hash")).thenReturn(false);

            assertThatThrownBy(() -> authService.changePassword(request, principal))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Current password");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("is refused when the new password equals the current one")
        void changePassword_sameAsCurrent() {
            User user = bankUserRequiringChange();
            CustomUserPrincipal principal = TestData.principal(user.getId(), RoleName.BANK);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Old@1234", "old-hash")).thenReturn(true);
            when(passwordEncoder.matches("New@12345", "old-hash")).thenReturn(true);

            assertThatThrownBy(() -> authService.changePassword(request, principal))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("different");

            verify(userRepository, never()).save(any());
        }
    }
}
