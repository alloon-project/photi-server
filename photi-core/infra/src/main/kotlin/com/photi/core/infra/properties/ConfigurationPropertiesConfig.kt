package com.photi.core.infra.properties

import com.photi.core.domain.common.properties.KakaoOAuthProperties
import com.photi.core.infra.PhotiConfig
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [KakaoOAuthProperties::class])
class ConfigurationPropertiesConfig : PhotiConfig
