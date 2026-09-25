package com.vidyasahay.vidyasahay.jwt;

import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link JwtTokenProvider}.
 * <p>
 * The provider has no external collaborators, so it is exercised directly
 * (no Mockito mocks are required here) with a throwaway HS256 secret.
 */
@DisplayName("JwtTokenProvider")
class JwtTokenProviderTest {

    private static final long EXPIRATION_MS = 3_600_000L;

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        // 256-bit (32 byte) random key, base64 encoded, as required by HS256.
        String secret = Base64.getEncoder().encodeToString(Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes()).getEncoded());
        tokenProvider = new JwtTokenProvider(secret, EXPIRATION_MS);
    }

    private CustomUserPrincipal principal() {
        return new CustomUserPrincipal(
                UUID.randomUUID(), "Jane", "Doe", "jane@example.com",
                "hashed-password", RoleName.STUDENT, true, false
        );
    }

    @Test
    @DisplayName("generates a token whose subject round-trips back to the user's email")
    void generateToken_extractEmail_roundTrips() {
        CustomUserPrincipal principal = principal();

        String token = tokenProvider.generateToken(principal);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.extractEmail(token)).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("marks a freshly generated token as valid")
    void isValid_freshToken_true() {
        String token = tokenProvider.generateToken(principal());

        assertThat(tokenProvider.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("marks a malformed token as invalid instead of throwing")
    void isValid_malformedToken_false() {
        assertThat(tokenProvider.isValid("not-a-real-jwt")).isFalse();
    }

    @Test
    @DisplayName("marks a token signed with a different key as invalid")
    void isValid_tokenSignedWithDifferentKey_false() {
        String otherSecret = Base64.getEncoder().encodeToString(Keys.hmacShaKeyFor(
                "abcdefghijabcdefghijabcdefghij12".getBytes()).getEncoded());
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherSecret, EXPIRATION_MS);

        String tokenFromOtherProvider = otherProvider.generateToken(principal());

        assertThat(tokenProvider.isValid(tokenFromOtherProvider)).isFalse();
    }

    @Test
    @DisplayName("exposes the configured expiration in milliseconds")
    void getExpirationMs_returnsConfiguredValue() {
        assertThat(tokenProvider.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }
}
