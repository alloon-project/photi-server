package com.photi.core.infra.oauth.adapter

import com.photi.core.domain.common.properties.GoogleOAuthProperties
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.port.OAuthPort
import com.photi.core.infra.oauth.client.GoogleOAuthClient
import com.photi.core.infra.oauth.port.JwtOidcPort
import org.springframework.stereotype.Component

@Component
class GoogleOAuthAdapter(
    private val googleOAuthProperties: GoogleOAuthProperties,
    private val googleOAuthClient: GoogleOAuthClient,
    private val jwtOidcPort: JwtOidcPort,
) : OAuthPort {

    override fun getIdTokenPayload(idToken: String): OidcPayload {
        val publicKeys = googleOAuthClient.getOidcPublicKeys()
        val kid = jwtOidcPort.getKidFromUnsignedIdToken(
            idToken,
            googleOAuthProperties.baseUrl,
            getClientId(idToken),
        )
        val jwk = publicKeys.keys.first { it.kid == kid }
        return jwtOidcPort.getIdTokenPayload(idToken, jwk.n, jwk.e)
    }

    override fun createOAuthInfo(sub: String) = OAuthInfo.ofGoogle(sub)

    private fun getClientId(idToken: String): String {
        val aud = jwtOidcPort.getAudFromUnsignedIdToken(idToken)
        val aosClientId = googleOAuthProperties.aosClientId
        return if (aud == aosClientId) aosClientId else googleOAuthProperties.iosClientId
    }
}
