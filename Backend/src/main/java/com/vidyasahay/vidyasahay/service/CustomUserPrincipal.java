package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserPrincipal implements UserDetails {

    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String hashedPassword;
    private final RoleName role;
    private final boolean active;
    private final boolean mustChangePassword;
    private final boolean profileCompleted;

    public CustomUserPrincipal(
            UUID userId,
            String firstName,
            String lastName,
            String email,
            String hashedPassword,
            RoleName role,
            boolean active,
            boolean mustChangePassword,
            boolean profileCompleted
    ) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.active = active;
        this.mustChangePassword = mustChangePassword;
        this.profileCompleted = profileCompleted;
    }

    public static CustomUserPrincipal from(User user) {
        return new CustomUserPrincipal(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getHashedPassword(),
                user.getRole().getName(),
                user.isActive(),
                user.isMustChangePassword(),
                user.isProfileCompleted()
        );
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public RoleName getRole() {
        return role;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                )
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
