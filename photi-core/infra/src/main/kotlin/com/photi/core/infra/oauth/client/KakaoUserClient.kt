package com.photi.core.infra.oauth.client

import com.photi.core.infra.oauth.dto.UnlinkResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "KakaoUserClient", url = "https://kapi.kakao.com")
interface KakaoUserClient {

    @PostMapping("/v1/user/unlink", consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun unlink(@RequestHeader("Authorization") accessToken: String): UnlinkResponse
}
