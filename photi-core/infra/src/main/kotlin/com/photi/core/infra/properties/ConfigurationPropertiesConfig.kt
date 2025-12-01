package com.photi.core.infra.properties

import com.photi.core.domain.common.properties.AppleOAuthProperties
import com.photi.core.domain.common.properties.GoogleOAuthProperties
import com.photi.core.domain.common.properties.KakaoOAuthProperties
import com.photi.core.infra.PhotiConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
    value = [
        KakaoOAuthProperties::class,
        GoogleOAuthProperties::class,
        AppleOAuthProperties::class,
    ]
)
class ConfigurationPropertiesConfig : PhotiConfig
