package com.photi.core.infra.oauth.client

import com.photi.core.infra.oauth.dto.OidcPublicKeysResponse
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "KakaoOAuthClient", url = "https://kauth.kakao.com")
interface KakaoOAuthClient {

    @Cacheable(cacheNames = ["KakaoOidc"], cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    fun getOidcPublicKeys(): OidcPublicKeysResponse
}
