package com.be.byeoldam.config;

import java.util.List;

import com.be.byeoldam.common.filter.JWTFilter;
import com.be.byeoldam.common.filter.LoginFilter;
import com.be.byeoldam.common.jwt.JwtUtil;
import com.be.byeoldam.common.oauth.handler.OAuth2LoginSuccessHandler;
import com.be.byeoldam.common.oauth.service.CustomOAuth2UserService;
import com.be.byeoldam.domain.user.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler  oAuth2LoginSuccessHandler;

    // 비밀번호 암호화를 위해 사용 (Security에서 제공)
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 자동 설정 (순환 의존성 해결)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // AuthenticationManager 자동 설정 (직접 @Bean 등록 X → Spring이 관리하도록 함)
    private AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager, UserService userService) throws Exception {
        //csrf보호를 비활성화.
        http.csrf((auth) -> auth.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource())); // ✅ CORS 설정 추가(swagger 사용 시 필요)
        //From 로그인 방식 비활성화  jwt 방식을 사용
        http.formLogin((auth) -> auth.disable());
        //http basic 인증 비활성화
        http.httpBasic((auth) -> auth.disable());
        // Spring Security Level 에서는 Session 사용 비활성화, Spring MVC Level에서 직접 사용하는 것은 가능.
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /*
         * 경로별 인가 작업. 모든 요청은 인증된 사용자만 접근 가능하며, 인증되지 않은 사용자가 접근하면 401에러를 발생.
         * 역할 이름 앞에 "ROLE_"라는 접두사를 자동으로 붙임 -> hasRole("ADMIN")은 ROLE_ADMIN을 검사
         * */
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config").permitAll() //swagger
                .requestMatchers("/login", "/api/users/register","/api/users/refresh","/api/users/email/send","/api/users/email/verify").permitAll() //모든 사용자
                .requestMatchers("/api/oauth2/**").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN") //role이 ADMIN인 유저만(추후에 수정하기)
                .requestMatchers("/","/api/test").permitAll()
                .anyRequest().authenticated()); //나머지는 로그인한 유저만

        http.addFilterAt(new LoginFilter(authenticationManager(), jwtUtil, userService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil, userDetailsService), LoginFilter.class);
//        // 커스텀한 로그아웃 필터 사용하기
//       http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        LoginFilter loginFilter = new LoginFilter(authenticationManager(), jwtUtil, userService);
        loginFilter.setFilterProcessesUrl("/api/users/login");
        http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil, userDetailsService), LoginFilter.class);


        // ✅ OAuth2 로그인 설정 추가
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler) // ✅ 로그인 성공 후 JWT 발급
        );

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of(
//        "chrome-extension://djiemmdlekojnbiclbgdepjapkoejddm",
//        "https://byeoldam.store",
//        "https://www.byeoldam.store",
//        "http://localhost:5173",
//        "http://localhost:5174"
//        ));
        configuration.setAllowedOriginPatterns(List.of("*")); // ✅ 모든 Origin 허용
        configuration.addAllowedMethod("*"); // ✅ 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
        configuration.addAllowedHeader("*"); // ✅ 모든 헤더 허용
        configuration.setAllowCredentials(false); // ✅ 쿠키, Authorization 헤더 포함 가능

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
}
}
