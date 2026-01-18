package com.retrip.trip.infra.config;

import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExample;
import com.retrip.trip.domain.exception.annotation.ApiErrorCodeExamples;
import com.retrip.trip.domain.exception.common.ErrorCode;
import com.retrip.trip.infra.adapter.in.presentation.rest.common.ErrorResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
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

    @Bean
    public OperationCustomizer errorCodeCustomizer() {
        return (operation, handlerMethod) -> {

            ApiErrorCodeExample single =
                    handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);

            ApiErrorCodeExamples multiple =
                    handlerMethod.getMethodAnnotation(ApiErrorCodeExamples.class);

            if (single != null) {
                addErrorResponse(operation, single.value());
            }

            if (multiple != null) {
                for (ErrorCode errorCode : multiple.value()) {
                    addErrorResponse(operation, errorCode);
                }
            }

            return operation;
        };
    }

    private void addErrorResponse(Operation operation, ErrorCode errorCode) {
        ApiResponses responses = operation.getResponses();
        String statusCode = String.valueOf(errorCode.getStatus().value());

        ApiResponse apiResponse = responses.computeIfAbsent(
                statusCode,
                k -> new ApiResponse().description(errorCode.getStatus().name() + " (message may vary by business rule)")
        );

        if (apiResponse.getContent() == null) {
            apiResponse.setContent(new Content());
        }

        MediaType mediaType = apiResponse.getContent()
                .computeIfAbsent("application/json", k -> new MediaType());

        ErrorResponse exampleBody = ErrorResponse.of(
                errorCode,
                "Current API URL",
                "Current API Method"
        );

        Example example = new Example();
        example.setValue(exampleBody);

        mediaType.addExamples(errorCode.name(), example);
    }
}
