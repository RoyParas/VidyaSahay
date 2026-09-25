package com.vidyasahay.vidyasahay.config;

import com.vidyasahay.vidyasahay.jwt.JwtAuthenticationFilter;
import com.vidyasahay.vidyasahay.service.CustomUserDetailsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtFilter;
	private final CustomUserDetailsService userDetailsService;
	
	@Value("${app.cors.allowed-origins}") String clientUrl;
	

	public SecurityConfig(JwtAuthenticationFilter jwtFilter, CustomUserDetailsService userDetailsService) {
		this.jwtFilter = jwtFilter;
		this.userDetailsService = userDetailsService;
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http
	            .csrf(csrf -> csrf.disable())
	    				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
	            .sessionManagement(session ->
	                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                    .requestMatchers(
	                            "/api/auth/login",
	                            "/api/auth/register",
	                            "/swagger-ui/**",
	                            "/swagger-ui.html",
	                            "/v3/api-docs/**")
	                    .permitAll()
	                    .anyRequest()
	                    .authenticated())
	            .authenticationProvider(authenticationProvider())
	            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}
	

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

	    CorsConfiguration configuration = new CorsConfiguration();

	    configuration.setAllowedOrigins(
	            List.of(clientUrl));

	    configuration.setAllowedMethods(
	            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

	    configuration.setAllowedHeaders(
	            List.of("*"));

	    configuration.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();

	    source.registerCorsConfiguration("/**", configuration);

	    return source;
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
