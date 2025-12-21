package com.photi.core.domain.user.port

import com.photi.core.domain.common.properties.OAuthProperties
import com.photi.core.domain.user.dto.OidcPayload
import com.photi.core.domain.user.model.OAuthInfo

interface OAuthPort {

    fun getIdTokenPayload(idToken: String, iss: String, aud: String): OidcPayload

    fun getProperties(): OAuthProperties

    fun createOAuthInfo(sub: String): OAuthInfo
}
