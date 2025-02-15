package com.be.byeoldam.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.be.byeoldam.common.resolver.LoginUserResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserResolver loginUserResolver;

    public WebConfig(LoginUserResolver loginUserResolver) {
        this.loginUserResolver = loginUserResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "https://byeoldam.store:5173","https://byeoldam.store","http://3.35.55.187:5173","http://frontend:3000","http://frontend:5173","http://127.0.0.1:8080", "http://localhost:5173", "http://localhost:5174") // Swagger UI와 관련된 Origin 허용
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "PUT") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 인증 정보(쿠키 등) 허용
    }

        @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserResolver);
    }
}