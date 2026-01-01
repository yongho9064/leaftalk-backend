package com.example.leaftalk.global.security.filter;

import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.entity.Role;
import com.example.leaftalk.global.security.details.MemberDetails;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // 매 요청마다 JWT 토큰의 유효성을 검사하는 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new ServletException("Invalid JWT token");
        }

        String accessToken = authorization.split(" ")[1];

        if (jwtUtil.isValid(accessToken, TokenType.ACCESS)) {

            String email = jwtUtil.getEmail(accessToken);
            String role = jwtUtil.getRole(accessToken);

            Member member = Member.builder()
                                  .email(email)
                                  .role(Role.from(role)).build();

            MemberDetails memberDetails = new MemberDetails(member);
            Authentication auth = new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰 만료 또는 유효하지 않은 토큰\"}");
        }

    }
}
