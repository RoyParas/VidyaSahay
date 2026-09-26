package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.ChangePasswordRequest;
import com.vidyasahay.vidyasahay.dto.request.LoginRequest;
import com.vidyasahay.vidyasahay.dto.response.LoginResponse;
import com.vidyasahay.vidyasahay.dto.request.StudentRegistrationRequest;
import com.vidyasahay.vidyasahay.dto.response.StudentRegistrationResponse;
import com.vidyasahay.vidyasahay.dto.response.AuthenticatedUserResponse;
import com.vidyasahay.vidyasahay.dto.response.ChangePasswordResponse;
import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.jwt.JwtTokenProvider;
import com.vidyasahay.vidyasahay.repository.RoleRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.AuthService;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import jakarta.transaction.Transactional;

import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
  

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,RoleRepository roleRepository,UserRepository userRepository,PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.roleRepository=roleRepository;
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authenticationManager
                        .authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        request.email().trim(),
                                        request.password()
                                )
                        )
                        .getPrincipal();

        AuthenticatedUserResponse userResponse =
                new AuthenticatedUserResponse(
                        principal.getUserId(),
                        principal.getFirstName(),
                        principal.getLastName(),
                        principal.getUsername(),
                        principal.getRole(),
                        principal.isMustChangePassword(),
                        principal.isProfileCompleted()
                );

        return new LoginResponse(
                tokenProvider.generateToken(principal),
                tokenProvider.getExpirationMs() / 1000,
                userResponse
        );
    }
    
    @Override
    @Transactional
    public StudentRegistrationResponse registerStudent(
            StudentRegistrationRequest request
    ) {
        String normalizedEmail = normalizeEmail(request.email());
        String normalizedMobile = request.mobile().trim();

        validateUniqueEmail(normalizedEmail);
        validateUniqueMobile(normalizedMobile);

        Role studentRole = roleRepository
                .findByName(RoleName.STUDENT).orElseThrow(() -> new BusinessException(
                        "STUDENT role is not configured in the roles table"
                ));

        User user = new User();

        user.setRole(studentRole);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(normalizedEmail);
        user.setMobile(normalizedMobile);

        user.setHashedPassword(
                passwordEncoder.encode(request.password())
        );

        user.setMustChangePassword(false);
        user.setProfileCompleted(false);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return new StudentRegistrationResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getMobile(),
                savedUser.getRole().getName(),
                true,
                "Student registered successfully. Please complete your profile."
        );
    }

    private void validateUniqueEmail(String email) throws BusinessException {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(
                    "An account already exists with this email"
            );
        }
    }

    private void validateUniqueMobile(String mobile) throws BusinessException {
        if (userRepository.existsByMobile(mobile)) {
            throw new BusinessException(
                    "An account already exists with this mobile number"
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Override
@Transactional
public ChangePasswordResponse changePassword(
        ChangePasswordRequest request,
        CustomUserPrincipal principal
) {
    User user = userRepository.findById(principal.getUserId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Authenticated user was not found"
                    )
            );

    RoleName roleName = user.getRole().getName();

    if (roleName != RoleName.BANK
            && roleName != RoleName.INSTITUTE) {
        throw new BusinessException(
                "Password change is allowed only for bank and institute users"
        );
    }

    if (!user.isMustChangePassword()) {
        throw new BusinessException(
                "Mandatory password change is not required for this account"
        );
    }

    if (!passwordEncoder.matches(
            request.currentPassword(),
            user.getHashedPassword()
    )) {
        throw new BusinessException(
                "Current password is incorrect"
        );
    }

    if (passwordEncoder.matches(
            request.newPassword(),
            user.getHashedPassword()
    )) {
        throw new BusinessException(
                "New password must be different from the current password"
        );
    }

    user.setHashedPassword(
            passwordEncoder.encode(request.newPassword())
    );

    user.setMustChangePassword(false);

    userRepository.save(user);

    return new ChangePasswordResponse(
            "Password changed successfully",
            false
    );
}
}





