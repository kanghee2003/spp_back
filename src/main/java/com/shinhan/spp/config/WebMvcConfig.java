package com.shinhan.spp.config;

import com.shinhan.spp.advice.CurrentUserArgumentResolver;
import com.shinhan.spp.interceptor.AuthenticationInterceptor;
import com.shinhan.spp.provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final JwtTokenProvider jwtTokenProvider;

    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor, @Value("${jwt.secret}") String secret) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.jwtTokenProvider = new JwtTokenProvider(secret);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**");
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(jwtTokenProvider));
    }
}
