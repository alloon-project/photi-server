package com.photi.core.infra.oauth.adapter

import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.port.OAuthPort
import com.photi.core.infra.oauth.client.KakaoOAuthClient
import com.photi.core.infra.oauth.port.JwtOidcPort
import org.springframework.stereotype.Component

@Component
class KakaoOAuthAdapter(
    private val kakaoOAuthClient: KakaoOAuthClient,
    private val jwtOidcPort: JwtOidcPort,
) : OAuthPort {

    override fun getIdTokenPayload(
        idToken: String,
        iss: String,
        aud: String,
        nonce: String,
    ): OidcPayload {
        val publicKeys = kakaoOAuthClient.getOidcPublicKeys()
        val kid = jwtOidcPort.getKidFromUnsignedIdToken(idToken, iss, aud, nonce)
        val jwk = publicKeys.keys.first { it.kid == kid }
        return jwtOidcPort.getIdTokenPayload(idToken, jwk.n, jwk.e)
    }
}
