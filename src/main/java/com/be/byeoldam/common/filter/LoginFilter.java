package com.be.byeoldam.common.filter;

import com.be.byeoldam.common.jwt.JwtUtil;
import com.be.byeoldam.domain.user.UserService;
import com.be.byeoldam.domain.user.dto.UserLoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
* 고민해볼 것: 필터에서 UserService를 주입 받아서 db에 refreshToken을 저장하는 방식이 맞을까...
* */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("-----LoginFilter.attemptAuthentication-----");
        //클라이언트 요청에서 email, password 추출(form-data로 던져줘야 함..)
        String email = obtainEmail(request);
        String password = obtainPassword(request);
        System.out.println(email+"," + password);
        //스프링 시큐리티에서 email과 password를 검증하기 위해서는 token에 담아야 함, 세번째 인자는 ROLE.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        //사용자 정보를 담은 token을 AuthenticationManager에 전달
        return authenticationManager.authenticate(authToken);
    }

    protected String obtainEmail(HttpServletRequest request) {
        return request.getParameter("email");
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        System.out.println("-----LoginFilter.successfulAuthentication-----");
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "로그인 성공 :)");

        String accessToken = jwtUtil.createJwt("access", userDetails.getUserId(), userDetails.getEmail(),"ROLE_USER",600000000L);
        String refreshToken = jwtUtil.createJwt("refresh", userDetails.getUserId(), userDetails.getEmail(),"ROLE_USER",600000000L); ;
        userService.updateRefreshToken(userDetails.getUserId(), refreshToken);

        UserLoginResponse userLoginResponse = UserLoginResponse.builder()
                .userId(userDetails.getUserId())
                .email(userDetails.getEmail())
                .nickname(userDetails.getNickname())
                .profileUrl(userDetails.getProfileUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
//        Map<String, Object> results = new HashMap<>();
//        results.put("userId", userDetails.getUserId());
//        results.put("email", userDetails.getEmail());
//        results.put("nickname", userDetails.getNickname());
//        results.put("accessToken", accessToken);
//        results.put("refreshToken", refreshToken);
        responseData.put("results", userLoginResponse);

        // JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseData); //예외처리 좀 더 생각해보기

        // 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException failed) throws IOException, ServletException {
        System.out.println("-----LoginFilter.unsuccessfulAuthentication-----");

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", false);
        responseData.put("message", "로그인 실패 :)");

        // JSON 문자열로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseData); //예외처리 좀 더 생각해보기

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
