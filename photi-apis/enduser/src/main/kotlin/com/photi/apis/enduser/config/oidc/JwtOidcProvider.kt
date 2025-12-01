package com.photi.apis.enduser.config.oidc

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
class JwtOidcProvider : JwtOidcPort {

    override fun getKidFromUnsignedIdToken(
        idToken: String,
        iss: String,
        aud: String,
        nonce: String,
    ) = getUnsignedIdTokenClaims(idToken, iss, aud, nonce)
        .header[KID]
        .toString()

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
        )
    }

    private fun getUnsignedIdTokenClaims(
        idToken: String,
        iss: String,
        aud: String,
        nonce: String,
    ): Jwt<Header, Claims> {
        return try {
            Jwts.parser()
                .requireIssuer(iss)
                .requireAudience(aud)
                .require(NONCE, nonce)
                .build()
                .parseUnsecuredClaims(getUnsignedIdToken(idToken))
        } catch (e: ExpiredJwtException) {
            throw GlobalException.ExpiredTokenException()
        } catch (e: Exception) {
            throw GlobalException.InvalidTokenException()
        }
    }

    private fun getUnsignedIdToken(idToken: String): String {
        val splitToken = idToken.split("\\.")
        if (splitToken.size != 3) {
            throw GlobalException.InvalidTokenException()
        }
        return "${splitToken[0]}.${splitToken[1]}."
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
        private const val EMAIL = "email"
        private const val NONCE = "nonce"
        private const val ALGORITHM = "RSA"
    }
}
