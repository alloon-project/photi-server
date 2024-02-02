package com.alloon.alloonserver.config.auth

import io.github.nefilim.kjwt.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

@Component
class JwtProvider(
    @Value("\${api.jwt.secret}")
    private var secret: String,

    @Value("\${api.jwt.domain}")
    private var domain: String,

    @Value("\${api.jwt.time.access}")
    private var accessTokenTime: String,

    @Value("\${api.jwt.time.refresh}")
    private var refreshTokenTime: String,
) {
    private val tokenPrefix: String = "Bearer"

    // TODO
//    fun createToken(userId: Long): HttpHeaders {
//    }

    // TODO
//    fun authenticate(token: String, jwtType: JwtTypeType) {
//
//    }

    // TODO
//    fun getUserId(token: String): Long {
//        return decodeToken(token).subject.toLong()
//    }

    // TODO
//    private fun decodeToken(token: String): DecodedJWT {
//    }
}
