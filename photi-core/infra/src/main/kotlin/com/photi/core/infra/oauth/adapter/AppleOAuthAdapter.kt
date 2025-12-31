package com.photi.core.infra.oauth.adapter

import com.photi.core.domain.common.properties.AppleOAuthProperties
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.port.OAuthPort
import com.photi.core.infra.oauth.client.AppleOAuthClient
import com.photi.core.infra.oauth.port.JwtOidcPort
import org.springframework.stereotype.Component

@Component
class AppleOAuthAdapter(
    private val appleOAuthProperties: AppleOAuthProperties,
    private val appleOAuthClient: AppleOAuthClient,
    private val jwtOidcPort: JwtOidcPort,
) : OAuthPort {

    override fun getIdTokenPayload(idToken: String): OidcPayload {
        val publicKeys = appleOAuthClient.getOidcPublicKeys()
        val kid = jwtOidcPort.getKidFromUnsignedIdToken(
            idToken,
            appleOAuthProperties.baseUrl,
            getClientId(idToken),
        )
        val jwk = publicKeys.keys.first { it.kid == kid }
        return jwtOidcPort.getIdTokenPayload(idToken, jwk.n, jwk.e)
    }

    override fun createOAuthInfo(sub: String) = OAuthInfo.ofApple(sub)

    private fun getClientId(idToken: String): String {
        val aud = jwtOidcPort.getAudFromUnsignedIdToken(idToken)
        val aosClientId = appleOAuthProperties.aosClientId
        return if (aud == aosClientId) aosClientId else appleOAuthProperties.iosClientId
    }
}
