package com.shinhan.spp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String bearer = "Authorization";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(bearer,
                                new SecurityScheme()
                                        .name(bearer)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(bearer))
                .info(this.info());
    }
    
    @Bean
    public OpenApiCustomizer globalHeaderOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) return;

            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(op -> {
                        // 필요하면 여기 추가
                        // addHeaderIfAbsent(op, "X-Dept-Code", "부서 코드");
                    })
            );
        };
    }

    private void addHeaderIfAbsent(io.swagger.v3.oas.models.Operation op, String name, String desc) {
        final boolean exists = op.getParameters() != null &&
                op.getParameters().stream().anyMatch(p -> name.equalsIgnoreCase(p.getName()) && "header".equals(p.getIn()));

        if (exists) return;

        op.addParametersItem(new Parameter()
                .in("header")
                .name(name)
                .required(false)
                .schema(new StringSchema())
                .description(desc));
    }

    private Info info() {
        return new Info()
                .title("정보보호포탈 API")
                .description("정보보호포탈 API Doc")
                .version("1.0");
    }
}
