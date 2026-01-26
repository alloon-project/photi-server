package com.photi.core.infra.oauth.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "AppleUserClient", url = "https://appleid.apple.com")
interface AppleUserClient {

    @PostMapping("/auth/revoke", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun unlink(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("token") accessToken: String,
    )
}
