package com.photi.apis.enduser.config.security

import com.photi.apis.enduser.config.security.JwtTokenProvider.Companion.AUTHORIZATION_HEADER
import com.photi.core.domain.common.exception.GlobalException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.NonceExpiredException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            resolveToken(request)?.let { accessToken ->
                val authentication = jwtTokenProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = authentication
            }
            filterChain.doFilter(request, response)
        } catch (e: GlobalException.ExpiredTokenException) {
            authenticationEntryPoint.commence(request, response, NonceExpiredException(e.message))
        } catch (e: GlobalException.InvalidTokenException) {
            authenticationEntryPoint.commence(
                request,
                response,
                InvalidOneTimeTokenException(e.message),
            )
        }
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader(AUTHORIZATION_HEADER)
        if (header != null && header.startsWith(BEARER)) {
            return header.substring(BEARER.length)
        }
        return null
    }

    companion object {
        const val BEARER = "Bearer "
    }
}
