package com.alloon.alloonserver.framework

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * 테스트용 MYSQL CONTAINER
 */
@Testcontainers
class TestContainerConfig() {


    companion object {
        const val DATABASE_NAME = "allon_server"
        const val USER_NAME = "allon"
        const val PASSWORD = "1234"
        const val INIT_SCHEMA_PATH = "init.sql"

        @JvmStatic
        val container: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:latest")
            .apply {
                withUsername(USER_NAME)
                withPassword(PASSWORD)
                withDatabaseName(DATABASE_NAME)
                withInitScript(INIT_SCHEMA_PATH)
            }
            .also { it.start() }

        @JvmStatic
        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { container.jdbcUrl }
            registry.add("spring.datasource.username") { container.username }
            registry.add("spring.datasource.password") { container.password }
        }
    }


    @PostConstruct
    fun startContainer() {
        container.start()
    }

    @PreDestroy
    fun stopContainer() {
        container.stop()
    }
}