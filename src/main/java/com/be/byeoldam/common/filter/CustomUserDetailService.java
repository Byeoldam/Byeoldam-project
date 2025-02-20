package com.be.byeoldam.common.filter;

import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("-----CustomUserDetailService.loadUserByUsername-----");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("이메일에 대응된 아이디 없음");
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        if(user.getIsActive() == User.AccountStatus.INACTIVE){
            throw new UsernameNotFoundException("탈퇴된 회원입니다.");
        }

        System.out.println("이메일에 대응된 아이디 있음");
        return CustomUserDetails.fromEntity(user);
    }
}
