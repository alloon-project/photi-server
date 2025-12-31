package com.photi.core.domain.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("oauth.google")
data class GoogleOAuthProperties(
    val baseUrl: String,
    val aosClientId: String,
    val iosClientId: String,
)
