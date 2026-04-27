package com.photi.core.domain.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("oauth.apple")
data class AppleOAuthProperties(
    val baseUrl: String,
    val restApiKey: String,
    val clientId: String,
    val clientSecret: String,
)
