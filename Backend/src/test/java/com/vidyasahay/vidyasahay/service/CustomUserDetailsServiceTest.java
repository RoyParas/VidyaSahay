package com.vidyasahay.vidyasahay.service;

import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("builds a CustomUserPrincipal with the ROLE_ prefixed authority")
    void loadUserByUsername_success() {
        User user = TestData.user(RoleName.BANK);

        when(userRepository.findByEmailIgnoreCase("jane@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("  jane@example.com  ");

        assertThat(details).isInstanceOf(CustomUserPrincipal.class);

        CustomUserPrincipal principal = (CustomUserPrincipal) details;
        assertThat(principal.getUserId()).isEqualTo(user.getId());
        assertThat(principal.getUsername()).isEqualTo("jane@example.com");
        assertThat(principal.getPassword()).isEqualTo("hashed-password");
        assertThat(principal.getRole()).isEqualTo(RoleName.BANK);
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_BANK");
    }

    @Test
    @DisplayName("reflects a deactivated account through isEnabled")
    void loadUserByUsername_inactiveUser() {
        User user = TestData.user(RoleName.STUDENT);
        user.setActive(false);

        when(userRepository.findByEmailIgnoreCase("jane@example.com"))
                .thenReturn(Optional.of(user));

        assertThat(userDetailsService.loadUserByUsername("jane@example.com").isEnabled()).isFalse();
    }

    @Test
    @DisplayName("rejects a null email without querying the database")
    void loadUserByUsername_nullEmail() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Email is missing");

        verify(userRepository, never()).findByEmailIgnoreCase(anyString());
    }

    @Test
    @DisplayName("rejects a blank email without querying the database")
    void loadUserByUsername_blankEmail() {
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("   "))
                .isInstanceOf(UsernameNotFoundException.class);

        verify(userRepository, never()).findByEmailIgnoreCase(anyString());
    }

    @Test
    @DisplayName("fails with a generic message when the account does not exist")
    void loadUserByUsername_notFound() {
        when(userRepository.findByEmailIgnoreCase("ghost@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("ghost@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
