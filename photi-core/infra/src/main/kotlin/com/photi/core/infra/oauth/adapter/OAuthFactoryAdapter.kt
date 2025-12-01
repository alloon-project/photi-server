package com.photi.core.infra.oauth.adapter

import com.photi.core.domain.user.model.OAuthProviderType
import com.photi.core.domain.user.port.OAuthFactoryPort
import org.springframework.stereotype.Component

@Component
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
