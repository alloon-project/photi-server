package com.photi.apis.enduser.config.security

import com.photi.core.domain.common.consts.CustomHttpHeaders.REFRESH_TOKEN
import com.photi.core.domain.common.exception.PhotiException
import com.photi.core.domain.common.exception.GlobalErrorCode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val requestURI = request.requestURI
            val accessToken = request.getHeader(AUTHORIZATION)
            val refreshToken = request.getHeader(REFRESH_TOKEN)

            if (requestURI.contains("/api/users/token")) {
                validateToken(refreshToken)
                jwtProvider.validateRefreshToken(refreshToken)
            } else if (!accessToken.isNullOrBlank()) {
                jwtProvider.validateAccessTokenAndSetAuthentication(accessToken)
            }

            filterChain.doFilter(request, response)
        } catch (ex: PhotiException) {
            customAuthenticationEntryPoint.commence(
                request,
                response,
                BadCredentialsException(ex.message),
            )
        }
    }

    private fun validateToken(token: String?) {
        if (token.isNullOrBlank()) {
            throw PhotiException(GlobalErrorCode.TOKEN_UNAUTHENTICATED)
        }
    }
}
