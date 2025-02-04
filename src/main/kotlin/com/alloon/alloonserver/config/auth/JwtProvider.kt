package com.alloon.alloonserver.config.auth

import arrow.core.Either
import arrow.core.getOrElse
import com.alloon.alloonserver.common.constant.CustomHttpHeaders
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.Role
import com.alloon.alloonserver.domain.user.UserRoleRepository
import io.github.nefilim.kjwt.*
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

        val authorities = userDetails.authorities.stream()
            .map { it.authority }
            .toList()

        val headers = HttpHeaders()

        val accessToken = JWT.hs256 {
            subject(userDetails.username)
            issuedAt(Instant.ofEpochMilli(time))
            expiresAt(Instant.ofEpochMilli(time + accessTokenTime))
            issuer(domain)
            claim("roles", authorities)
        }

        when (val signedJWT = accessToken.sign(secret)) {
            is Either.Left -> throw CustomException(SERVER_ERROR)
            is Either.Right -> headers.add(HttpHeaders.AUTHORIZATION, signedJWT.value.rendered)
        }

        if (authorities.contains(Role.MASTER.name))
            return headers

        val refreshToken = JWT.hs256 {
            subject(userDetails.username)
            issuedAt(Instant.ofEpochMilli(time))
            expiresAt(Instant.ofEpochMilli(time + refreshTokenTime))
            issuer(domain)
            claim("roles", authorities)
        }

        when (val signedJWT = refreshToken.sign(secret)) {
            is Either.Left -> throw CustomException(SERVER_ERROR)
            is Either.Right -> headers.add(
                CustomHttpHeaders.REFRESH_TOKEN,
                signedJWT.value.rendered
            )
        }

        return headers
    }

    fun verifyToken(token: String, jwtType: JwtType): JWT<JWSHMAC256Algorithm> {
        val jwt = when (val result =
            verifySignature<JWSHMAC256Algorithm>(token.removePrefix(tokenPrefix), secret)) {
            is Either.Left -> throw CustomException(TOKEN_UNAUTHENTICATED)
            is Either.Right -> result.value
        }

        val expirationTime =
            jwt.expiresAt().getOrElse { throw CustomException(TOKEN_UNAUTHENTICATED) }
        val now = Instant.now()

        if (expirationTime.isBefore(now)) {
            throw CustomException(TOKEN_UNAUTHENTICATED)
        }

        val userId = jwt.subject()
            .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
            .toLong()

        return when (jwtType) {
            JwtType.ACCESS -> {
                val user = getUserDetails(userId)
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(
                        user.username,
                        user.password,
                        user.authorities
                    )
                jwt
            }

            JwtType.REFRESH -> jwt
        }
    }

    private fun getUserDetails(userId: Long): User {
        val userRoles = userRoleRepository.findAllFetchUser(userId)

        val user = userRoles.stream()
            .findFirst()
            .orElseThrow { CustomException(USER_NOT_FOUND) }
            .user

        val grantedAuthorities = userRoles.stream()
            .map { userRole -> SimpleGrantedAuthority(userRole.role.name) }
            .toList()

        return User(user.id.toString(), user.password, grantedAuthorities)
    }
}
