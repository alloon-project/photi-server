package com.photi.core.domain.user.port

import com.photi.core.domain.user.dto.OidcPayload

interface OAuthPort {

    fun getIdTokenPayload(idToken: String, iss: String, aud: String, nonce: String): OidcPayload
}
