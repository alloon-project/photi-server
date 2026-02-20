package com.photi.core.infra.oauth.client

import com.photi.core.infra.oauth.dto.OidcPublicKeysResponse

interface OAuthClient {

    fun getOidcPublicKeys(): OidcPublicKeysResponse
}
