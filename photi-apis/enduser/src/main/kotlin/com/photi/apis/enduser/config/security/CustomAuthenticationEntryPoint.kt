package com.photi.apis.enduser.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.photi.apis.enduser.common.exception.dto.ErrorResponse
import com.photi.core.domain.common.exception.ExceptionCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?,
    ) {
        val errorResponse = ErrorResponse.of(ExceptionCode.TOKEN_UNAUTHENTICATED)

        response?.apply {
            status = UNAUTHORIZED.value()
            contentType = APPLICATION_JSON_VALUE
            characterEncoding = UTF_8.name()
        }

        objectMapper.writeValue(response?.writer, errorResponse)
    }
}
