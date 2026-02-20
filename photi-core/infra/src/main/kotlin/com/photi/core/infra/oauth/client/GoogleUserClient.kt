package com.photi.core.infra.oauth.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "GoogleUserClient", url = "https://oauth2.googleapis.com")
interface GoogleUserClient {

    @PostMapping("/revoke", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun unlink(@RequestParam("token") accessToken: String)
}
