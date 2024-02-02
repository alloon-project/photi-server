package com.alloon.alloonserver.config.auth

import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.response.ExceptionResponse
import com.alloon.alloonserver.common.constant.ExceptionCode
import com.alloon.alloonserver.common.constant.ExceptionCode.TOKEN_UNAUTHENTICATED
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {

    override fun commence(request: HttpServletRequest?,
                          response: HttpServletResponse?,
                          authException: AuthenticationException?) {
        response?.apply {
            status = HttpStatus.UNAUTHORIZED.value()
            contentType = APPLICATION_JSON_VALUE
        }

        try {
            val responseBody = objectMapper.writeValueAsString(ExceptionResponse(TOKEN_UNAUTHENTICATED))
            response?.outputStream?.use {outputStream -> {
                outputStream.write(responseBody.toByteArray())
                outputStream.flush()
            } }

        } catch (e: IOException) {
            throw CustomException(ExceptionCode.SERVER_ERROR, e.cause)
        }
    }
}