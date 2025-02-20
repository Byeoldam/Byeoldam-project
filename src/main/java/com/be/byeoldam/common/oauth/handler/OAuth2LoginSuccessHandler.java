package com.be.byeoldam.common.oauth.handler;

import com.be.byeoldam.common.jwt.JwtUtil;
import com.be.byeoldam.common.oauth.dto.CustomOAuth2User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public OAuth2LoginSuccessHandler(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        System.out.println("OAuth2 로그인 성공! JWT 생성 중...");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getName();
        String role = "USER"; // 기본 권한 설정 (필요하면 변경)

        // JWT 생성
        String accessToken = "token";

        // JSON 형태로 클라이언트에게 응답
        Map<String, String> tokens = Map.of("accessToken", accessToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokens));

        System.out.println("JWT 발급 완료: " + accessToken);
    }
}
