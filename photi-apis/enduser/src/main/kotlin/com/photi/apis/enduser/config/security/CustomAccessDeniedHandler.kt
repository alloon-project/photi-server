package com.photi.apis.enduser.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.photi.apis.enduser.common.exception.dto.ErrorResponse
import com.photi.core.domain.common.exception.GlobalErrorCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import kotlin.text.Charsets.UTF_8

@Component
class CustomAccessDeniedHandler(
    private val objectMapper: ObjectMapper,
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?,
    ) {
        val errorResponse = ErrorResponse.of(GlobalErrorCode.TOKEN_UNAUTHORIZED)
        response?.apply {
            status = FORBIDDEN.value()
            contentType = APPLICATION_JSON_VALUE
            characterEncoding = UTF_8.name()
            writer.write(objectMapper.writeValueAsString(errorResponse))
        }
    }
}
