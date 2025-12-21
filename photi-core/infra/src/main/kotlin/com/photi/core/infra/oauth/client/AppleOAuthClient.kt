package com.photi.core.infra.oauth.client

import com.photi.core.infra.oauth.dto.OidcPublicKeysResponse
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "AppleOAuthClient", url = "https://appleid.apple.com")
interface AppleOAuthClient : OAuthClient {

    @Cacheable(cacheNames = ["AppleOidc"], cacheManager = "oidcCacheManager")
    @GetMapping("/auth/keys")
    override fun getOidcPublicKeys(): OidcPublicKeysResponse
}
