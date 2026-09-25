package com.vidyasahay.vidyasahay.support;

import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Supplies a fixed {@link CustomUserPrincipal} for every controller parameter
 * of that type.
 * <p>
 * {@code MockMvcBuilders.standaloneSetup(...)} does not load Spring Security,
 * so {@code @AuthenticationPrincipal} is never resolved and the parameter
 * arrives as {@code null} — which turns every controller that dereferences the
 * principal into a NullPointerException and a misleading 500. Registering this
 * resolver via {@code .setCustomArgumentResolvers(...)} removes that whole
 * class of false failures without pulling in a security context.
 */
public class PrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final CustomUserPrincipal principal;

    public PrincipalArgumentResolver(CustomUserPrincipal principal) {
        this.principal = principal;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return CustomUserPrincipal.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        return principal;
    }
}
