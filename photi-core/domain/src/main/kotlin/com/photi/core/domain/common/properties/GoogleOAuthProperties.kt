package com.photi.core.domain.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("oauth.google")
data class GoogleOAuthProperties(
    override val baseUrl: String,
    override val nativeAppKey: String,
) : OAuthProperties
