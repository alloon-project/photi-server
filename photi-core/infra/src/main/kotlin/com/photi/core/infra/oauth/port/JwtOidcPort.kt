package com.photi.core.infra.oauth.port

import com.photi.core.domain.user.dto.OidcPayload

interface JwtOidcPort {

    fun getAudFromUnsignedIdToken(idToken: String): String

    fun getKidFromUnsignedIdToken(idToken: String, iss: String, aud: String): String

    fun getIdTokenPayload(idToken: String, modulus: String, exponent: String): OidcPayload

    fun getClientSecret(): String
}
