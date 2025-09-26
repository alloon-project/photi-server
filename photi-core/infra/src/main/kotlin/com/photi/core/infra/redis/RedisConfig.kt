package com.photi.core.infra.redis

import com.photi.core.infra.PhotiConfig
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfig(
    private val redisProperties: RedisProperties,
) : PhotiConfig {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfiguration =
            RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        redisConfiguration.password = RedisPassword.of(redisProperties.password)
        return LettuceConnectionFactory(redisConfiguration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<ByteArray, ByteArray> {
        val redisTemplate = RedisTemplate<ByteArray, ByteArray>()
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }
}
