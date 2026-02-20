package com.photi.core.domain.user.port

import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.model.OAuthInfo

interface OAuthPort {

    fun getIdTokenPayload(idToken: String): OidcPayload

    fun createOAuthInfo(sub: String): OAuthInfo

    fun withdraw(accessToken: String)
}
