package com.photi.server.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import redis.embedded.RedisServer
import java.io.File

@Profile("local, test")
@Configuration
class EmbeddedRedisConfig(
    private val redisProperties: RedisProperties,
) {
    private lateinit var redisServer: RedisServer

    @PostConstruct
    fun startRedis() {
        redisServer = if (isArmMac()) RedisServer(getRedisFileForArmMac(), redisProperties.port)
        else RedisServer(redisProperties.port)

        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }

    private fun isArmMac(): Boolean {
        return System.getProperty("os.arch") == "aarch64" &&
                System.getProperty("os.name") == "Mac OS X"
    }

    private fun getRedisFileForArmMac(): File {
        return ClassPathResource("binary/redis/redis-server-mac-arm64").file
    }

}