package com.be.byeoldam.common.filter;

import com.be.byeoldam.common.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JWTFilter.doFilterInternal");

        // 로그인 요청일 경우, 토큰이 없어도 패스
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/users/login") || requestURI.contains("/api/users/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String token = request.getHeader("accessToken");
        System.out.println(token);
        // 1. 토큰이 없다면 다음 필터(로그인 필터)로 넘김
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(token);
        } catch (ExpiredJwtException e) {
            PrintWriter writer = response.getWriter();
            writer.print("token이 만료 되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        // 3. access 토큰이 있는 경우
        String email = jwtUtil.getUserEmail(token);
        // UserDetails 객체 생성
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // SecurityContextHolder에 인증 정보 저장
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        request.setAttribute("userId", customUserDetails.getUserId());
        filterChain.doFilter(request, response);
    }
}
