package com.photi.server.config.auth

import arrow.core.Either
import arrow.core.getOrElse
import com.photi.server.common.constant.CustomHttpHeaders
import com.photi.server.common.constant.ExceptionCode.*
import com.photi.server.common.response.CustomException
import com.photi.server.domain.user.Role
import com.photi.server.domain.user.UserRoleRepository
import io.github.nefilim.kjwt.JWSHMAC256Algorithm
import io.github.nefilim.kjwt.JWT
import io.github.nefilim.kjwt.sign
import io.github.nefilim.kjwt.verifySignature
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtProvider(
    @Value("\${api.jwt.secret}")
    private var secret: String,

    @Value("\${api.jwt.domain}")
    private var domain: String,

    @Value("\${api.jwt.time.access}")
    private var accessTokenTime: Long,

    @Value("\${api.jwt.time.refresh}")
    private var refreshTokenTime: Long,

    private val userRoleRepository: UserRoleRepository,
) {
    private val tokenPrefix: String = "Bearer "

    fun createToken(userId: Long): HttpHeaders {
        val userDetails = getUserDetails(userId)
        val time = System.currentTimeMillis()
        val authorities = userDetails.authorities.map { it.authority }

        val headers = HttpHeaders()

        val accessToken = JWT.hs256 {
            subject(userDetails.username)
            issuedAt(Instant.ofEpochMilli(time))
            expiresAt(Instant.ofEpochMilli(time + accessTokenTime))
            issuer(domain)
            claim("roles", authorities)
        }.signOrThrow()

        headers.add(HttpHeaders.AUTHORIZATION, accessToken)

        if (!authorities.contains(Role.MASTER.name)) {
            val refreshToken = JWT.hs256 {
                subject(userDetails.username)
                issuedAt(Instant.ofEpochMilli(time))
                expiresAt(Instant.ofEpochMilli(time + refreshTokenTime))
                issuer(domain)
                claim("roles", authorities)
            }.signOrThrow()

            headers.add(CustomHttpHeaders.REFRESH_TOKEN, refreshToken)
        }

        return headers
    }

    fun validateAccessTokenAndSetAuthentication(token: String) {
        val jwt = validateSignatureOrThrow(token.removePrefix(tokenPrefix))
        val expirationTime = jwt.expiresAt()
            .getOrElse { throw CustomException(TOKEN_UNAUTHENTICATED) }
        if (expirationTime.isBefore(Instant.now())) {
            throw CustomException(TOKEN_UNAUTHENTICATED)
        }

        val userId = jwt.subject()
            .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
            .toLong()
        val user = getUserDetails(userId)

        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(user.username, user.password, user.authorities)
    }

    fun validateRefreshToken(token: String) {
        val jwt = validateSignatureOrThrow(token)
        val expirationTime = jwt.expiresAt()
            .getOrElse { throw CustomException(TOKEN_UNAUTHENTICATED) }
        if (expirationTime.isBefore(Instant.now())) {
            throw CustomException(TOKEN_UNAUTHENTICATED)
        }

        jwt.subject()
            .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
    }

    fun getUserId(refreshToken: String): Long {
        val jwt = validateSignatureOrThrow(refreshToken)
        return jwt.subject()
            .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
            .toLong()
    }

    private fun getUserDetails(userId: Long): User {
        val userRoles = userRoleRepository.findAllFetchUser(userId)

        val user = userRoles.stream()
            .findFirst()
            .orElseThrow { CustomException(USER_NOT_FOUND) }
            .user

        val grantedAuthorities = userRoles.map { SimpleGrantedAuthority("ROLE_${it.role.name}") }

        return User(user.id.toString(), user.password, grantedAuthorities)
    }

    private fun validateSignatureOrThrow(token: String): JWT<JWSHMAC256Algorithm> {
        return when (val result = verifySignature<JWSHMAC256Algorithm>(token, secret)) {
            is Either.Left -> throw CustomException(TOKEN_UNAUTHENTICATED)
            is Either.Right -> result.value
        }
    }

    private fun JWT<JWSHMAC256Algorithm>.signOrThrow(): String {
        return when (val signedJWT = this.sign(secret)) {
            is Either.Left -> throw CustomException(SERVER_ERROR)
            is Either.Right -> signedJWT.value.rendered
        }
    }
}
