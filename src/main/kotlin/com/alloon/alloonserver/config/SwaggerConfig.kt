package com.alloon.alloonserver.config

import com.alloon.alloonserver.common.constant.CustomHttpHeaders
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
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

    @Bean
    fun operationCustomizer(): OperationCustomizer {
        return OperationCustomizer { operation, _ ->
            addResponseBodySchemaExample(operation)
            operation
        }
    }

    private fun addResponseBodySchemaExample(operation: Operation) {
        val filterKeys = operation.responses.filterKeys { it.startsWith("2") }

        filterKeys.forEach { (code, response) ->
            response.content.forEach { (_, mediaType) ->
                val data = mediaType.schema
                val schema = Schema<String>().apply {
                    addProperty("code", Schema<String>().example(code))
                    addProperty("message", Schema<String>().example("성공"))
                    addProperty("data", data)
                }
                mediaType.schema = schema
            }
        }
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "Access Token"
        const val REFRESH_TOKEN_KEY = "Refresh Token"
    }
}