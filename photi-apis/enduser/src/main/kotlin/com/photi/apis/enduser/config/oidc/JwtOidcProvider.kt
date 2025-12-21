package com.photi.apis.enduser.config.oidc

import com.fasterxml.jackson.databind.ObjectMapper
import com.photi.core.domain.common.exception.GlobalException
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.infra.oauth.port.JwtOidcPort
import io.jsonwebtoken.*
import org.springframework.stereotype.Component
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class JwtOidcProvider(
    private val objectMapper: ObjectMapper,
) : JwtOidcPort {

    override fun getKidFromUnsignedIdToken(idToken: String, iss: String, aud: String): String {
        val splitIdToken = getSplitIdToken(idToken)
        validatePayload(splitIdToken[1], iss, aud)
        val headerJson = String(Base64.getUrlDecoder().decode(splitIdToken[0]))
        val headerMap = objectMapper.readValue(headerJson, Map::class.java)
        return headerMap[KID].toString()
    }

    override fun getIdTokenPayload(
        idToken: String,
        modulus: String,
        exponent: String,
    ): OidcPayload {
        val claims = getIdTokenClaims(idToken, modulus, exponent)
        return OidcPayload(
            claims.issuer,
            claims.audience.first(),
            claims.subject,
            claims[EMAIL].toString(),
            claims[PICTURE].toString(),
        )
    }

    private fun getSplitIdToken(idToken: String): List<String> {
        val splitToken = idToken.split(".")
        if (splitToken.size != 3) {
            throw GlobalException.InvalidTokenException()
        }
        return splitToken
    }

    private fun validatePayload(payload: String, iss: String, aud: String) {
        val payloadJson = String(Base64.getUrlDecoder().decode(payload))
        val payloadMap = objectMapper.readValue(payloadJson, Map::class.java)
        if (payloadMap[ISS] != iss || payloadMap[AUD] != aud) {
            throw GlobalException.InvalidTokenException()
        }
        val exp = (payloadMap[EXP] as? Number)?.toLong()
            ?: throw GlobalException.InvalidTokenException()
        if (Date().time / 1000 > exp) throw GlobalException.ExpiredTokenException()
    }

    private fun getIdTokenClaims(idToken: String, modulus: String, exponent: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(getRsaPublicKey(modulus, exponent))
                .build()
                .parseSignedClaims(idToken)
                .payload
        } catch (e: ExpiredJwtException) {
            throw GlobalException.ExpiredTokenException()
        } catch (e: Exception) {
            throw GlobalException.InvalidTokenException()
        }
    }

    private fun getRsaPublicKey(modulus: String, exponent: String): PublicKey {
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val decodeN = Base64.getUrlDecoder().decode(modulus)
        val decodeE = Base64.getUrlDecoder().decode(exponent)
        val n = BigInteger(1, decodeN)
        val e = BigInteger(1, decodeE)
        val keySpec = RSAPublicKeySpec(n, e)
        return keyFactory.generatePublic(keySpec)
    }

    companion object {
        private const val KID = "kid"
        private const val ISS = "iss"
        private const val AUD = "aud"
        private const val EXP = "exp"
        private const val EMAIL = "email"
        private const val PICTURE = "picture"
        private const val ALGORITHM = "RSA"
    }
}
