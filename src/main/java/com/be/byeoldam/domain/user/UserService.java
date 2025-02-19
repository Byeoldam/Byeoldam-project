package com.be.byeoldam.domain.user;

import com.be.byeoldam.common.jwt.JwtUtil;
import com.be.byeoldam.domain.user.dto.*;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.domain.user.util.S3Util;
import com.be.byeoldam.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final S3Util s3Util;
    // Redis와 통신하기 위해 사용되는 RedisTemplate의 문자열 전용 버전
    private final StringRedisTemplate stringRedisTemplate;
    private final Duration REDIS_EXPIRATION = Duration.ofMinutes(10);

    @Value("${mail.username}")
    private String senderEmail;

    // 이메일 인증번호 발송(이메일 중복 확인 + 유효 코드 보내기)
    void sendEmailVerificationCode(UserEmailRequest userEmailRequest) {
        System.out.println("UserService.sendEmailVerificationCode");

        String newEmail = userEmailRequest.getEmail();
        if(userRepository.existsByEmail(newEmail)) {
            throw new CustomException("이미 존재하는 이메일이니다.");
        }

        // 6자리 인증번호
        String verificationCode = generateVerificationCode();
        System.out.println("UserService.sendEmailVerificationCode:"  + verificationCode);

        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(newEmail); // 받는 사람 이메일
        message.setSubject("[별담] 이메일 인증 코드 안내"); // 이메일 제목
        message.setText("안녕하세요, 별담(BYEOLDAM)입니다.\n\n"
                + "요청하신 이메일 인증 코드를 안내드립니다.\n\n"
                + "인증 코드: " + verificationCode + "\n\n"
                + "인증 코드는 10분 동안만 유효합니다. 인증을 완료해 주세요.\n\n"
                + "감사합니다.\n"
                + "- 별담 팀 드림"); // 이메일 본문
        message.setFrom(senderEmail+"@naver.com"); // 보내는 사람 이메일 (네이버 이메일 주소), secret에 넣은거랑 같아야만 함.
        javaMailSender.send(message); // 이메일 전송

        //Redis에 저장
        String key = newEmail;
        String value = verificationCode;
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(key, value, REDIS_EXPIRATION);
        System.out.println("Redis 저장 완료");
    }


   // 인증 코드 확인
    void checkVerificationCode(UserVerificationCodeRequest userVerificationCode) {
        String key = userVerificationCode.getEmail();
        String code = userVerificationCode.getVerificationCode();

        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String value = stringValueOperations.get(key);

        if(!value.equals(code)) {
            throw new CustomException("입력하신 인증번호가 일치하지 않습니다,");
        }

        stringRedisTemplate.delete(key);
    }

    // 회원 가입
    @Transactional
    UserRegisterResponse registerUser(UserRegisterRequest registerRequest) {
        try {
            User user = registerRequest.toEntity();
            // 비밀번호 암호화 풀기
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.encodePassword(encodedPassword);
            //user.encodePassword(user.getPassword());

            user.updateProfileImage(s3Util.getDefaultProfileImageUrl());
            user = userRepository.save(user);

            Map<String, String> tokens = generateTokens(user);
            user.updateRefreshToken(tokens.get("refresh"));
            userRepository.save(user);

            UserRegisterResponse userRegisterResponse = UserRegisterResponse.fromEntity(user);
            userRegisterResponse.addTokens(tokens.get("access"), tokens.get("refresh"));

            return userRegisterResponse;
        } catch (DataIntegrityViolationException e) {
            throw new CustomException("이미 존재하는 이메일입니다.");
        }
    }

    // 로그인
    @Transactional
    UserLoginResponse login(UserLoginRequest userLoginRequest){
        User user = userRepository.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(() -> new CustomException("이메일을 다시 한번 확인해주세요."));

        if(!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){
            throw new CustomException("비밀번호가 일치하지 않습니다.");
        }

        Map<String, String> tokens = generateTokens(user);
        user.updateRefreshToken(tokens.get("refresh"));
        userRepository.save(user);

        UserLoginResponse userLoginResponse = UserLoginResponse.fromEntity(user);
        userLoginResponse.addTokens(tokens.get("access"), tokens.get("refresh"));

        return userLoginResponse;
    }

    // 구글 로그인
    @Transactional
    UserLoginResponse googleLogin(String email, String nickname) {
        System.out.println("UserService.googleLogin");

        // 구글 회원이 신규 일 경우 저장하기.
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .provider(User.Provider.GOOGLE)
                        .nickname(nickname)
                        .password("**")
                        .build()));
        System.out.println("USER:" + user.getNickname());
        // token 불러오기
        Map<String, String> tokens = generateTokens(user);
        user.updateRefreshToken(tokens.get("refresh"));
        UserLoginResponse userLoginResponse = UserLoginResponse.fromEntity(user);
        userLoginResponse.addTokens(tokens.get("access"), tokens.get("refresh"));

        return userLoginResponse;
    }
        // 로그아웃
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException("로그인된 사용자가 없습니다.");
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("유저를 찾을 수 없습니다."));

        user.updateRefreshToken(null);
        userRepository.save(user);
    }

    // refreshToken을 통해 access토큰 재발급
    @Transactional
    public UserTokenResponse refreshToken(String refreshToken) {
        Long userId = jwtUtil.getUserId(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("올바르지 않은 RefreshToken입니다."));

        if(!user.getRefreshToken().equals(refreshToken)){
            throw new CustomException("올바르지 않은 RefreshToken입니다.");
        }

        Map<String, String> tokens = generateTokens(user);
        user.updateRefreshToken(tokens.get("refresh"));

        return UserTokenResponse.of(tokens.get("access"), tokens.get("refresh"));
    }

    // JWT필터에서 refreshToken 업데이트 하기
    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);
    }

    private Map<String, String> generateTokens(User user) {
        String accessToken = jwtUtil.createJwt("access", user.getId(), user.getEmail(), user.getRole(), 6000000L); // 10시간(추후개선)
        String refreshToken = jwtUtil.createJwt("refresh", user.getId(), user.getEmail(), user.getRole(), 86400000L); //24시간

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access", accessToken);
        tokens.put("refresh", refreshToken);
        return tokens;
    }

    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999 범위의 숫자 생성
        return String.valueOf(code);
    }
}
