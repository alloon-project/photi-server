package com.photi.core.infra.feign

import com.photi.core.infra.PhotiConfig
import com.photi.core.infra.oauth.client.BaseFeignClientsPackage
import feign.Logger
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackageClasses = [BaseFeignClientsPackage::class])
class FeignConfig : PhotiConfig {

    @Bean
    fun feignLoggerLevel() = Logger.Level.FULL
}
