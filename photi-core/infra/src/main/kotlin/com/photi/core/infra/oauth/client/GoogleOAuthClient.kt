package com.photi.core.infra.oauth.client

import com.photi.core.infra.oauth.dto.OidcPublicKeysResponse
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "GoogleOAuthClient", url = "https://www.googleapis.com")
interface GoogleOAuthClient : OAuthClient {

    @Cacheable(cacheNames = ["GoogleOidc"], cacheManager = "oidcCacheManager")
    @GetMapping("/oauth2/v3/certs")
    override fun getOidcPublicKeys(): OidcPublicKeysResponse
}
