package com.retrip.trip.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private final String jwtSchemeName = "jwtAuth";

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        jwtSchemeName,
                                        new SecurityScheme()
                                                .name(jwtSchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("JWT 토큰")
                                )
                ).info(
                        new Info()
                                .title("Trip Application")
                                .version("v0.0.1")
                )

                ;
    }

}
