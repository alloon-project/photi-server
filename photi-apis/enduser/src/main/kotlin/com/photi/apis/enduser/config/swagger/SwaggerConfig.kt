package com.photi.apis.enduser.config.swagger

import com.photi.apis.enduser.common.exception.annotation.ApiErrorResponses
import com.photi.core.domain.common.consts.CustomHttpHeaders
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.consts.SwaggerKey.REFRESH_TOKEN_KEY
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.web.method.HandlerMethod

@OpenAPIDefinition(
    info = Info(
        title = "포티 API",
        description = "포티 API 명세서입니다.",
        version = "v1"
    )
)
@Configuration
@Profile("!prod")
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
        return OperationCustomizer { operation, handlerMethod ->
            addResponseBodySchemaExample(operation)
            addApiErrorResponses(operation, handlerMethod)
            operation
        }
    }

    private fun addResponseBodySchemaExample(operation: Operation) {
        val filterKeys = operation.responses.filterKeys { it.startsWith("2") }
        filterKeys.forEach { (code, response) ->
            response.content.forEach { (_, mediaType) ->
                val schema = Schema<String>().apply {
                    val codeProperty =
                        Schema<String>().example("$code ${response.description.uppercase()}")
                    addProperty("code", codeProperty)
                    addProperty("message", Schema<String>().example("성공"))
                    addProperty("data", mediaType.schema)
                }
                mediaType.schema = schema
            }
        }
    }

    private fun addApiErrorResponses(operation: Operation, handlerMethod: HandlerMethod) {
        val apiErrorResponses = handlerMethod.method.getAnnotation(ApiErrorResponses::class.java)
        val responses = operation.responses
        val errorCodes = apiErrorResponses.errorCodeClass.java.enumConstants
        errorCodes?.forEach { errorCode ->
            val example = Example().apply {
                summary = errorCode.code
                value = mapOf("code" to errorCode.code, "message" to errorCode.message)
                description = errorCode.description
            }
            val apiResponse = responses[errorCode.status.toString()]
            val mediaType = apiResponse?.content?.get("application/json") ?: MediaType()
            val examples = mediaType.examples?.toMutableMap() ?: mutableMapOf()
            examples[errorCode.code] = example
            val content = Content().addMediaType("application/json", MediaType().examples(examples))
            responses.addApiResponse(errorCode.status.toString(), ApiResponse().content(content))
        }
    }
}
