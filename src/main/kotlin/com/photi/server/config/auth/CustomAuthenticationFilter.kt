package com.photi.server.config.auth

import com.photi.server.common.constant.CustomHttpHeaders.Companion.REFRESH_TOKEN
import com.photi.server.common.constant.ExceptionCode.TOKEN_UNAUTHENTICATED
import com.photi.server.common.response.CustomException
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
        filterChain: FilterChain
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
        } catch (ex: CustomException) {
            customAuthenticationEntryPoint.commence(
                request,
                response,
                BadCredentialsException(ex.message)
            )
        }
    }

    private fun validateToken(token: String?) {
        if (token.isNullOrBlank()) {
            throw CustomException(TOKEN_UNAUTHENTICATED)
        }
    }
}