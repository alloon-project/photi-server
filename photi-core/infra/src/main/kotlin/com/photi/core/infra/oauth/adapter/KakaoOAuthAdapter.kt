package com.photi.core.infra.oauth.adapter

import com.photi.core.domain.common.properties.KakaoOAuthProperties
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.model.OAuthInfo
import com.photi.core.domain.user.port.OAuthPort
import com.photi.core.infra.oauth.client.KakaoOAuthClient
import com.photi.core.infra.oauth.client.KakaoUserClient
import com.photi.core.infra.oauth.port.JwtOidcPort
import org.springframework.stereotype.Component

@Component
class KakaoOAuthAdapter(
    private val kakaoOAuthProperties: KakaoOAuthProperties,
    private val kakaoOAuthClient: KakaoOAuthClient,
    private val kakaoUserClient: KakaoUserClient,
    private val jwtOidcPort: JwtOidcPort,
) : OAuthPort {

    override fun getIdTokenPayload(idToken: String): OidcPayload {
        val publicKeys = kakaoOAuthClient.getOidcPublicKeys()
        val kid = jwtOidcPort.getKidFromUnsignedIdToken(
            idToken,
            kakaoOAuthProperties.baseUrl,
            kakaoOAuthProperties.restApiKey,
        )
        val jwk = publicKeys.keys.first { it.kid == kid }
        return jwtOidcPort.getIdTokenPayload(idToken, jwk.n, jwk.e)
    }

    override fun createOAuthInfo(sub: String) = OAuthInfo.ofKakao(sub)

    override fun withdraw(accessToken: String) {
        kakaoUserClient.unlink(BEARER + accessToken)
    }

    companion object {
        private const val BEARER = "Bearer "
    }
}
