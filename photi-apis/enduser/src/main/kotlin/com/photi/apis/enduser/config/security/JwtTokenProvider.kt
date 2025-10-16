package com.photi.apis.enduser.config.security

import com.photi.core.domain.common.exception.GlobalException
import com.photi.core.domain.user.model.RoleType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${spring.security.jwt.secret-key}")
    private val secretKey: String,
    @Value("\${spring.security.jwt.access-exp}")
    private val accessExp: Long,
    @Value("\${spring.security.jwt.refresh-exp}")
    private val refreshExp: Long,
) {

    fun createToken(id: Long, role: RoleType): HttpHeaders {
        val now = Instant.now()
        val headers = HttpHeaders()
        val accessToken = Jwts.builder()
            .subject(id.toString())
            .claim(ROLE, role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(accessExp)))
            .signWith(getSecretKey())
            .compact()
        headers.add(AUTHORIZATION_HEADER, accessToken)
        if (role != RoleType.ADMIN) {
            val refreshToken = Jwts.builder()
                .subject(id.toString())
                .expiration(Date.from(now.plusSeconds(refreshExp)))
                .signWith(getSecretKey())
                .compact()
            headers.add(REFRESH_TOKEN_HEADER, refreshToken)
        }
        return headers
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims = parseClaims(accessToken)
        val principal = CustomUserDetails(claims.subject, claims[ROLE].toString())
        return UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    }

    fun getUserIdBy(refreshToken: String) = parseClaims(refreshToken).subject.toLong()

    fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(getSecretKey() as SecretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw GlobalException.ExpiredTokenException()
        } catch (e: Exception) {
            throw GlobalException.InvalidTokenException()
        } ?: throw GlobalException.InvalidTokenException()
    }

    private fun getSecretKey() = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val REFRESH_TOKEN_HEADER = "Refresh-Token"
        private const val ROLE = "role"
        private const val COMMA = ","
    }
}
