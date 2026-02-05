package com.shinhan.spp.config;

import com.shinhan.spp.advice.UserArgumentResolver;
import com.shinhan.spp.interceptor.AuthenticationInterceptor;
import com.shinhan.spp.service.SampleService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final SampleService sampleService;

    public WebMvcConfig(AuthenticationInterceptor authenticationInterceptor, SampleService sampleService) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.sampleService = sampleService;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**");
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserArgumentResolver(sampleService));
    }
}
