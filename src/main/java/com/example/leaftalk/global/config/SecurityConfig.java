package com.example.leaftalk.global.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/members/signup").permitAll()
                .requestMatchers("/admin").hasAnyRole("ADMIN")
                .requestMatchers("/my/**").hasAnyRole("USER")
                .anyRequest()
                .permitAll());

        http.cors(cors -> cors.configurationSource(configurationSource()));

        http.csrf(csrf -> csrf.disable());

        // 동시 로그인 제한
        http.sessionManagement(auth -> auth
                .maximumSessions(1) // 최대 세션 수 설정
                .maxSessionsPreventsLogin(true)); // 최대 세션 수 초과 시 새로운 로그인 차단

        // 세션 고정 보호
        http.sessionManagement(auth -> auth.sessionFixation().changeSessionId());

        http.formLogin(form -> form
                .usernameParameter("email")
                .successHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK))
                .failureHandler((req, res, ex) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);  // 인증 정보 허용
        configuration.setMaxAge(3600L);           // 캐싱 시간 설정 (같은 요청 시 재요청 방지)

        // CORS 설정을 모든 경로에 적용 (/login, /signup... 등)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Role Hierarchy 설정 (상위 권한이 하위 권한을 포함) ADMIN > USER
    @Bean
    public RoleHierarchy roleHierarchy() {

        return RoleHierarchyImpl.withDefaultRolePrefix()
                                .role("ADMIN").implies("USER")
                                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
