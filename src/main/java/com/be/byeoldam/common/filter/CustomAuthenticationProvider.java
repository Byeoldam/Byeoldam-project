package com.be.byeoldam.common.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component  // @Bean으로 등록할 필요 없이, Spring이 자동으로 관리
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println("-----CustomAuthenticationProvider.authenticate-----");

        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        System.out.println(email + ", " + password);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        System.out.println(userDetails);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("이메일과 비밀번호 불일치");
            throw new BadCredentialsException("이메일과 비밀번호 불일치");
        }

        // 우선 비밀번호 암호화 풀어놓기.
//        if(!password.equals(userDetails.getPassword())) {
//            System.out.println("이메일과 비밀번호 불일치");
//            throw new BadCredentialsException("이메일과 비밀번호 불일치");
//        }

        System.out.println("Success");
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
