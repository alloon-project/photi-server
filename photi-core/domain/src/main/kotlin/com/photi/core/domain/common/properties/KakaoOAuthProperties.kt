package com.photi.core.domain.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("oauth.kakao")
data class KakaoOAuthProperties(
    override val baseUrl: String,
    override val nativeAppKey: String,
) : OAuthProperties
