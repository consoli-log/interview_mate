package com.interviewmate.be.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * packageName  : com.interviewmate.be.common.config
 * fileName     : SwaggerConfig
 * author       : eumsoli
 * date         : 2025-03-05
 * description  : Swagger 설정
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정을 정의하는 Bean
     *
     * @return OpenAPI 객체
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("인터뷰 메이트 API")
                        .description("인터뷰 메이트 API 명세서")
                        .version("v1.0")
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 서버")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

}
