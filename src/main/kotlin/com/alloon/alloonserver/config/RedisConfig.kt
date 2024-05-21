package com.alloon.alloonserver.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!local & !test")
@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties,
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config.useSingleServer()
            .setAddress("redis://" + redisProperties.host + ":" + redisProperties.port)
            .setPassword(redisProperties.password)
        return Redisson.create(config)
    }
}