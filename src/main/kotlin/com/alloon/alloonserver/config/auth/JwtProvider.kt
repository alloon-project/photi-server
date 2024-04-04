package com.alloon.alloonserver.config.auth

import arrow.core.Either
import arrow.core.getOrElse
import com.alloon.alloonserver.common.constant.ExceptionCode
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

    /**
     * JWT 토큰 생성
     * @param userId 회원 식별자
     * @throws SERVER_ERROR 500
     * @return HTTP 헤더
     */
    fun createToken(userId: Long): HttpHeaders {
        val userDetails = getUserDetails(userId)
        val time = System.currentTimeMillis()

        val authorities = userDetails.authorities.stream()
            .map { it.authority }
            .toList()

        val headers: HttpHeaders = HttpHeaders()

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

        val refreshToken = JWT.hs256() {
            subject(userDetails.username)
            issuedAt(Instant.ofEpochMilli(time))
            expiresAt(Instant.ofEpochMilli(time + refreshTokenTime))
            issuer(domain)
            claim("roles", authorities)
        }

        when (val signedJWT = refreshToken.sign(secret)) {
            is Either.Left -> throw CustomException(SERVER_ERROR)
            is Either.Right -> headers.add("Refresh-Token", signedJWT.value.rendered)
        }

        return headers
    }

    /**
     * JWT 토큰 검증
     * @param token 토큰
     * @param jwtType 토큰 타입
     * @throws TOKEN_UNAUTHENTICATED 401
     * @throws TOKEN_UNAUTHORIZED 403
     * @return JWT 토큰
     */
    fun verifyToken(token: String, jwtType: JwtType): JWT<JWSHMAC256Algorithm> {
        return when (val jwt = verifySignature<JWSHMAC256Algorithm>(token.substring(tokenPrefix.length), secret)) {
            is Either.Left -> throw CustomException(TOKEN_UNAUTHENTICATED)
            is Either.Right -> {
                val user = getUserDetails(jwt.value.subject()
                    .getOrElse { throw CustomException(TOKEN_UNAUTHORIZED) }
                    .toLong())

                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(user.username, user.password, user.authorities)
                jwt.value
            }
        }
    }

    /**
     * 토큰 해독
     * @param token 토큰
     * @throws TOKEN_UNAUTHENTICATED 401
     * @return 해독 JWT 토큰
     */
    private fun decodeToken(token: String): DecodedJWT<JWSHMAC256Algorithm> {
        require(token.isEmpty()) {
            throw CustomException(TOKEN_UNAUTHENTICATED)
        }

        return when (val decodedJwt = JWT.decodeT(token.substring(tokenPrefix.length), JWSHMAC256Algorithm)) {
            is Either.Left -> throw CustomException(TOKEN_UNAUTHENTICATED)
            is Either.Right -> decodedJwt.value
        }
    }

    /**
     * 회원 정보 조회
     * @param userId 회원 식별자
     * @throws USER_NOT_FOUND 404
     * @return 회원 정보
     */
    private fun getUserDetails (userId: Long): User {
        val userRoles = userRoleRepository.findAllFetchUser(userId)

        val user = userRoles.stream()
            .findFirst()
            .orElseThrow{ CustomException(ExceptionCode.USER_NOT_FOUND) }
            .user

        val grantedAuthorities = userRoles.stream()
            .map { userRole -> SimpleGrantedAuthority(userRole.role.name) }
            .toList()

        return User(user.id.toString(), user.password, grantedAuthorities)
    }
}
