package com.be.byeoldam.domain.user;

import com.be.byeoldam.common.ResponseTemplate;
import com.be.byeoldam.common.annotation.UserId;
import com.be.byeoldam.common.filter.CustomUserDetails;
import com.be.byeoldam.domain.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "User 관련 API")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @Operation(
            summary = "회원가입",
            description = "새로운 사용자 등록, 이후 사용자 정의 태그도 입력해야 하기 때문에 로그인과 동일하게 사용자 정보 제공"
    )
    @PostMapping("/register")
    public ResponseTemplate<UserRegisterResponse> registerUser(@RequestBody UserRegisterRequest request) {
       UserRegisterResponse response = userService.registerUser(request);
       return ResponseTemplate.ok(response);
    }

    // Filter에서 반환되어, 컨트롤러까지 오지 않지만 혹시 몰라서 추가
    @Operation(
            summary = "로그인",
            description = "json이 아닌 form-data로 전달해주세요! 아래의 데이터 외에도 별도로 필요한 데이터가 더 있다면 알려주세요"
    )
    @PostMapping("/login")
    public ResponseTemplate<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        UserLoginResponse response = userService.login(request);
        return ResponseTemplate.ok(response);
    }

    //해당 컨트롤러에서 이메일 중복 체크등도 함께 구현하기
    @Operation(
            summary = "이메일 인증 코드 요청",
            description = "사용자의 이메일 주소로 인증 코드 전송. Redis가 도커 환경에서 실행되면 실제 구현이 추가될 예정.(아직 구현 X)"
    )
    @PostMapping("/email/send")
    public ResponseTemplate<String> sendVerificationEmail(@RequestBody UserEmailRequest request){
        userService.sendEmailVerificationCode(request);
        return ResponseTemplate.ok("이메일 인증 코드가 전송되었습니다.");
    }

    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "사용자가 입력한 인증 코드가 올바른지 검증. Redis가 도커 환경에서 실행되면 실제 구현이 추가될 예정.(아직 구현 X)"
    )
    @PostMapping("/email/verify")
    public ResponseTemplate<String> verifyEmailCode(@RequestBody UserVerificationCodeRequest request) {
        userService.checkVerificationCode(request);
        return ResponseTemplate.ok("인증 완료!");
    }

    @Operation(
            summary = "로그아웃",
            description = "RefreshToken을 서버에서 제거하며, AccessToken은 클라이언트에서 직접 삭제해야 합니다, 아래와 같이 따로 전송해야하는 데이터는 없고 API만 호출해줘!"
    )
    @PostMapping("/logout")
    public ResponseTemplate<String> logout(){
        userService.logout();
        return ResponseTemplate.ok("로그아웃이 완료되었습니다.");
    }

    @Operation(
            summary = "refreshToken을 통해 Access토큰 재발급",
            description = "아래 형식에 맞게 RefreshToken을 던져주면, 토큰을 재발급!"
    )
    @PostMapping("/refresh")
    public ResponseTemplate<UserTokenResponse> refreshToken(@RequestBody UserTokenRequest userTokenRequest){
        UserTokenResponse response = userService.refreshToken(userTokenRequest.getRefreshToken());
        return ResponseTemplate.ok(response);
    }

    @Operation(
            summary = "filter test용",
            description = "올바른 accessToken을 넣고 요청을 한번 해보세요!"
    )
    @GetMapping("/me")
    public ResponseTemplate<String> me(@UserId Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseTemplate.ok("유저ID:" + userId + "고생했어!");
    }

}
