package com.be.byeoldam.domain.user;

import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.domain.user.dto.GoogleLoginRequest;
import com.be.byeoldam.domain.user.dto.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private final UserService userService;

    @PostMapping("/token")
    public ResponseTemplate<UserLoginResponse> getUserInfo(@RequestBody GoogleLoginRequest request) {
        System.out.println("OAuth2Controller.getUserInfo");
        System.out.println("clientId: " + clientId);
        System.out.println("clientSecret: " + clientSecret);
        System.out.println("code: " + request.getCode());
        System.out.println("redirectUri: " + request.getRedirectUri());

        // ✅ Google OAuth2 토큰 엔드포인트로 요청 보냄
        String googleTokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "code", request.getCode(),
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", request.getRedirectUri(),
                "grant_type", "authorization_code"
        );

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> googleResponse;
        try {
            googleResponse = restTemplate.exchange(googleTokenUrl, HttpMethod.POST, requestEntity, Map.class);
        } catch (Exception e) {
            System.out.println("전송 실패");
            return ResponseTemplate.fail("RestTemplate 전송 실패");
        }

        System.out.println("RestTemplate 전송성공");

        // ✅ Google 응답에서 액세스 토큰 추출
        Map<String, Object> responseBody = googleResponse.getBody();
        if (responseBody == null || !responseBody.containsKey("access_token")) {
            return ResponseTemplate.fail("토큰을 추출하지 못함");
        }

        String googleAccessToken = (String) responseBody.get("access_token");

        // ✅ Google UserInfo API 호출 (사용자 정보 가져오기)
        String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(googleAccessToken);

        HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse;
        try {
            userInfoResponse = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, userInfoRequest, Map.class);
        } catch (Exception e) {
            return ResponseTemplate.fail("구글에서 유저 정보를 가져올 수 없음");
        }

        String email = (String) userInfoResponse.getBody().get("email");
        String name = (String) userInfoResponse.getBody().get("name");

        System.out.println("email: " + email);
        System.out.println("name: " + name);
        UserLoginResponse userLoginResponse = userService.googleLogin(email, name);
        System.out.println(userLoginResponse.toString());
        // ✅ Google에서 받은 사용자 정보 반환
        return ResponseTemplate.ok(userLoginResponse);
    }

}
