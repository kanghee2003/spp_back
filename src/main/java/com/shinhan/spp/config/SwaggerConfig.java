package com.shinhan.spp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(this.info())
                ;
    }

    private Info info() {
        return new Info()
                .title("정보보호포탈 API")
                .description("정보보호포탈 API Doc")
                .version("1.0")
                ;
    }
}
