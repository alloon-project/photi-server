package com.photi.core.domain.user.port

import com.photi.core.domain.user.model.OAuthProviderType

interface OAuthFactoryPort {

    fun getOAuthAdapter(provider: OAuthProviderType): OAuthPort
}
