package com.alloon.alloonserver.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "포티 API",
        description = "포티 API 명세서입니다.",
        version = "v1"
    )
)
@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val jwtSchemeName = "JWT"
        val securityRequirement = SecurityRequirement().addList(jwtSchemeName)
        val components = Components().addSecuritySchemes(
            jwtSchemeName, SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER_TOKEN_PREFIX)
                .bearerFormat(jwtSchemeName)
        )

        return OpenAPI()
            .addSecurityItem(securityRequirement)
            .components(components)
    }

    companion object {
        private const val BEARER_TOKEN_PREFIX = "Bearer"
    }
}