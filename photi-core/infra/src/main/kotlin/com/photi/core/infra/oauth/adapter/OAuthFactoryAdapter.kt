package com.photi.core.infra.oauth.adapter

import com.photi.core.domain.user.model.OAuthProviderType
import com.photi.core.domain.user.port.OAuthFactoryPort
import com.photi.core.infra.oauth.port.JwtOidcPort
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(JwtOidcPort::class)
class OAuthFactoryAdapter(
    private val kakaoOAuthAdapter: KakaoOAuthAdapter,
    private val googleOAuthAdapter: GoogleOAuthAdapter,
    private val appleOAuthAdapter: AppleOAuthAdapter,
) : OAuthFactoryPort {

    override fun getOAuthAdapter(provider: OAuthProviderType) = when (provider) {
        OAuthProviderType.KAKAO -> kakaoOAuthAdapter
        OAuthProviderType.GOOGLE -> googleOAuthAdapter
        OAuthProviderType.APPLE -> appleOAuthAdapter
    }
}
