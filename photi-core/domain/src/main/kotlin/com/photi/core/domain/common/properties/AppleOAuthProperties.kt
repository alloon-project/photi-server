package com.photi.core.domain.common.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("oauth.apple")
@ConditionalOnProperty(prefix = "oauth.apple", name = ["base-url"])
data class AppleOAuthProperties(
    val baseUrl: String,
    val restApiKey: String,
    val clientId: String,
    val clientSecret: String,
)
