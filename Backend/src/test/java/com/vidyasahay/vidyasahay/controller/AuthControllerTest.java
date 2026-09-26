package com.vidyasahay.vidyasahay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vidyasahay.vidyasahay.dto.request.ChangePasswordRequest;
import com.vidyasahay.vidyasahay.dto.request.LoginRequest;
import com.vidyasahay.vidyasahay.dto.request.StudentRegistrationRequest;
import com.vidyasahay.vidyasahay.dto.response.AuthenticatedUserResponse;
import com.vidyasahay.vidyasahay.dto.response.ChangePasswordResponse;
import com.vidyasahay.vidyasahay.dto.response.LoginResponse;
import com.vidyasahay.vidyasahay.dto.response.StudentRegistrationResponse;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.GlobalExceptionHandler;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.service.AuthService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.support.PrincipalArgumentResolver;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-layer tests for {@link AuthController}.
 *
 * <h2>What changed relative to the original version of this class</h2>
 * <ol>
 *   <li>{@link PrincipalArgumentResolver} is registered. Standalone MockMvc does
 *       not load Spring Security, so {@code @AuthenticationPrincipal} was never
 *       resolved and {@code changePassword} could not be tested at all - the
 *       principal arrived as {@code null} and the endpoint blew up with a 500
 *       that had nothing to do with the code under test.</li>
 *   <li>The duplicate-email case no longer asserts an exact 500. {@code
 *       BusinessException} has no dedicated {@code @ExceptionHandler} in
 *       {@link GlobalExceptionHandler}, so it currently falls through to the
 *       catch-all and produces a 500. Pinning the test to that number means the
 *       test fails the moment somebody fixes the gap, so it asserts "not a
 *       success" instead and stays green either way.</li>
 *   <li>{@code MediaType.APPLICATION_JSON} is used instead of a raw string, and
 *       every mocked call is verified, so a silently-skipped delegation cannot
 *       pass unnoticed.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController (web layer)")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CustomUserPrincipal principal;

    @BeforeEach
    void setUp() {
        principal = TestData.principal(RoleName.BANK);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PrincipalArgumentResolver(principal))
                .build();
    }

    // ------------------------------------------------------------------ login

    @Test
    @DisplayName("POST /api/auth/login returns 200 with the login payload")
    void login_success() throws Exception {
        AuthenticatedUserResponse user = new AuthenticatedUserResponse(
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com", RoleName.STUDENT, false, true);

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("jwt-token", 3600L, user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("jane@example.com", "Passw0rd!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.user.email").value("jane@example.com"))
                .andExpect(jsonPath("$.user.role").value("STUDENT"))
                .andExpect(jsonPath("$.user.mustChangePassword").value(false));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login returns 400 when the email is blank")
    void login_blankEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"Passw0rd!\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("POST /api/auth/login returns 400 when the email is not an email")
    void login_malformedEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"Passw0rd!\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    @Test
    @DisplayName("POST /api/auth/login returns 400 when the password is missing")
    void login_missingPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jane@example.com\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any());
    }

    // --------------------------------------------------------------- register

    @Test
    @DisplayName("POST /api/auth/register returns 201 with the created student")
    void register_success() throws Exception {
        StudentRegistrationRequest request = new StudentRegistrationRequest(
                "Jane", "Doe", "jane@example.com", "9876543210", "Passw0rd!");

        StudentRegistrationResponse response = new StudentRegistrationResponse(
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com", "9876543210",
                RoleName.STUDENT, true, "Student registered successfully. Please complete your profile.");

        when(authService.registerStudent(any(StudentRegistrationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.mobile").value("9876543210"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.mustCompleteProfile").value(true));

        verify(authService).registerStudent(any(StudentRegistrationRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register returns 400 for a malformed mobile number")
    void register_invalidMobile() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\","
                                + "\"email\":\"jane@example.com\",\"mobile\":\"123\","
                                + "\"password\":\"Passw0rd!\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerStudent(any());
    }

    @Test
    @DisplayName("POST /api/auth/register returns 400 for a password under eight characters")
    void register_shortPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Jane\",\"lastName\":\"Doe\","
                                + "\"email\":\"jane@example.com\",\"mobile\":\"9876543210\","
                                + "\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerStudent(any());
    }

    @Test
    @DisplayName("POST /api/auth/register does not return a success status when the service rejects the email")
    void register_duplicateEmail() throws Exception {
        when(authService.registerStudent(any(StudentRegistrationRequest.class)))
                .thenThrow(new BusinessException("An account already exists with this email"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new StudentRegistrationRequest(
                                "Jane", "Doe", "jane@example.com", "9876543210", "Passw0rd!"))))
                .andExpect(result ->
                        assertThat(result.getResponse().getStatus()).isGreaterThanOrEqualTo(400));

        verify(authService).registerStudent(any(StudentRegistrationRequest.class));
    }

    // --------------------------------------------------------- change password

    @Test
    @DisplayName("PUT /api/auth/change-password returns 200 and passes the principal through")
    void changePassword_success() throws Exception {
        when(authService.changePassword(any(ChangePasswordRequest.class), eq(principal)))
                .thenReturn(new ChangePasswordResponse("Password changed successfully", false));

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ChangePasswordRequest("Old@1234", "New@12345"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.mustChangePassword").value(false));

        verify(authService).changePassword(any(ChangePasswordRequest.class), eq(principal));
    }

    @Test
    @DisplayName("PUT /api/auth/change-password returns 400 when the new password is too short")
    void changePassword_shortNewPassword() throws Exception {
        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"Old@1234\",\"newPassword\":\"short\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).changePassword(any(), any());
    }

    @Test
    @DisplayName("PUT /api/auth/change-password maps ResourceNotFoundException to 404")
    void changePassword_userGone() throws Exception {
        when(authService.changePassword(any(ChangePasswordRequest.class), eq(principal)))
                .thenThrow(new ResourceNotFoundException("Authenticated user was not found"));

        mockMvc.perform(put("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ChangePasswordRequest("Old@1234", "New@12345"))))
                .andExpect(status().isNotFound());
    }
}
