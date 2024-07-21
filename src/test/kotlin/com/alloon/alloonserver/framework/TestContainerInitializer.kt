package com.alloon.alloonserver.framework

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import kotlin.math.log
import org.springframework.context.ConfigurableApplicationContext as ConfigurableApplicationContext1

class TestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext1> {

    val logger : Logger = LoggerFactory.getLogger(TestContainerInitializer::class.java)

    override fun initialize(applicationContext: ConfigurableApplicationContext1) {
        val jdbcUrl = TestContainerConfig.container.jdbcUrl
        val username = TestContainerConfig.container.username
        val password = TestContainerConfig.container.password

        logger.info("jdbc :  $jdbcUrl")

        val values = TestPropertyValues.of(
            "spring.datasource.url=$jdbcUrl",
            "spring.datasource.username=$username",
            "spring.datasource.password=$password"
        )
        values.applyTo(applicationContext)
    }
}