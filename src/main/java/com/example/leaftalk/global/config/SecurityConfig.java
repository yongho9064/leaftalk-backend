package com.example.leaftalk.global.config;

import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.domain.member.entity.Role;
import com.example.leaftalk.global.security.filter.JWTFilter;
import com.example.leaftalk.global.security.filter.LoginFilter;
import com.example.leaftalk.global.security.handler.RefreshTokenLogoutHandler;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(
            @Qualifier("loginSuccessHandler") AuthenticationSuccessHandler successHandler,
            @Qualifier("socialSuccessHandler") AuthenticationSuccessHandler socialSuccessHandler,
            AuthenticationConfiguration authenticationConfiguration) {
        this.successHandler = successHandler;
        this.socialSuccessHandler = socialSuccessHandler;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthService authService, JWTUtil jwtUtil) {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/jwt/exchange", "/jwt/refresh", "/members/exist", "/members").permitAll()
                .anyRequest().authenticated()
        );

        // 커스텀 로그인 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), LogoutFilter.class);
        http.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), successHandler), UsernamePasswordAuthenticationFilter.class);

        // 기본 로그인 페이지, CSRF, HTTP Basic Auth 비활성화
        http.formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // 세션을 사용하지 않음 (Stateless)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 예외 처리
        http.exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> response
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler(((request, response, accessDeniedException) -> response
                        .sendError(HttpServletResponse.SC_FORBIDDEN))));

        http.logout(logout -> logout
                .addLogoutHandler(new RefreshTokenLogoutHandler(authService, jwtUtil))
                .logoutSuccessHandler(((request, response, authentication) ->
                        response.setStatus(HttpServletResponse.SC_OK)
                )));

        http.oauth2Login(oauth2 -> oauth2.successHandler(socialSuccessHandler));

        http.cors(cors -> cors.configurationSource(configurationSource()));

        return http.build();
    }

    // 상위 권한이 하위 권한을 포함 ADMIN > USER
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(Role.ADMIN.name()).implies(Role.USER.name())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));                                 // 요청 헤더 허용 -> 클라이언트가 보내는
        configuration.setAllowCredentials(true);                                       //  인증 정보 허용
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));       //  응답 헤더 노출 -> 클라이언트가 받을수 있는
        configuration.setMaxAge(3600L);                                                //  캐싱 시간 설정 (같은 요청 시 재요청 방지)

        // CORS 설정을 모든 경로에 적용 (/login, /signup... 등)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}