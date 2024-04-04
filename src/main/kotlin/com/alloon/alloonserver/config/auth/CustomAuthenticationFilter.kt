package com.alloon.alloonserver.config.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain) {
        val accessToken = request.getHeader(AUTHORIZATION)
        val refreshToken = request.getHeader("Refresh-Token")

        if (accessToken != null && !request.requestURI.contains("token")) {
            println(accessToken)
            jwtProvider.verifyToken(accessToken, JwtType.ACCESS)
        } else if (refreshToken != null) {
            println(refreshToken)
            jwtProvider.verifyToken(refreshToken, JwtType.REFRESH)
        }

        filterChain.doFilter(request, response)
    }


}