package com.alloon.alloonserver.config

import com.alloon.alloonserver.common.constant.CustomHttpHeaders
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

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
        val accessTokenSecurityScheme = SecurityScheme().apply {
            type = SecurityScheme.Type.HTTP
            scheme = "Bearer"
            bearerFormat = "JWT"
            name = HttpHeaders.AUTHORIZATION
        }

        val refreshTokenSecurityScheme = SecurityScheme().apply {
            type = SecurityScheme.Type.APIKEY
            `in` = SecurityScheme.In.HEADER
            name = CustomHttpHeaders.REFRESH_TOKEN
        }

        val components = Components()
            .addSecuritySchemes(ACCESS_TOKEN_KEY, accessTokenSecurityScheme)
            .addSecuritySchemes(REFRESH_TOKEN_KEY, refreshTokenSecurityScheme)

        return OpenAPI().components(components)
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "Access Token"
        const val REFRESH_TOKEN_KEY = "Refresh Token"
    }
}