package com.photi.apis.enduser.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.photi.apis.enduser.common.exception.dto.ErrorResponse
import com.photi.core.domain.common.exception.GlobalErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.www.NonceExpiredException
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
        response?.apply {
            status = UNAUTHORIZED.value()
            contentType = APPLICATION_JSON_VALUE
            characterEncoding = UTF_8.name()
            writer.write(objectMapper.writeValueAsString(getErrorResponse(authException)))
        }
    }

    private fun getErrorResponse(authException: AuthenticationException?) = when (authException) {
        is NonceExpiredException -> ErrorResponse.of(GlobalErrorCode.EXPIRED_TOKEN)
        is InvalidOneTimeTokenException -> ErrorResponse.of(GlobalErrorCode.INVALID_TOKEN)
        else -> ErrorResponse.of(GlobalErrorCode.TOKEN_UNAUTHENTICATED)
    }
}
