package com.vidyasahay.vidyasahay.support;

import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * {@code LoanSchemeServiceImpl} and {@code ScholarshipSchemeServiceImpl} read
 * the caller straight out of {@link SecurityContextHolder} instead of taking it
 * as a parameter, so their unit tests have to install one by hand.
 * <p>
 * Always pair {@link #authenticate} with {@link #clear} in an {@code @AfterEach}
 * — the holder is thread local and leaks into unrelated tests otherwise.
 */
public final class SecurityContextSupport {

    private SecurityContextSupport() {
    }

    public static void authenticate(CustomUserPrincipal principal) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
