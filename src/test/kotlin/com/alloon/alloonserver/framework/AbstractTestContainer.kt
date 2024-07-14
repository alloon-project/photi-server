package com.alloon.alloonserver.framework

import org.junit.jupiter.api.AfterAll
import org.slf4j.Logger
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

/**
 * 테스트용 MYSQL CONTAINER
 */
abstract class AbstractTestContainer(){


    companion object {

        val DATABASE_NAME = "allon_server"
        val USER_NAME = "allon"
        val PASSWORD = "1234"
        val INIT_SCHEMA_PATH = "init.sql"

        @JvmStatic
        private val container = PostgreSQLContainer("postgres:latest")
            .withUsername(USER_NAME)
            .withPassword(PASSWORD)
            .withDatabaseName(DATABASE_NAME)
            .withInitScript(INIT_SCHEMA_PATH)

        private val logger : Logger = org.slf4j.LoggerFactory.getLogger(AbstractTestContainer::class.java)

        init {
            logger.info("==== START TEST CONTAINER ====")
            container.start()

        }

        @DynamicPropertySource
        fun dynamicProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            val datasource = "jdbc:postgres://"  + container.host + ':' + container.firstMappedPort + "/alloon_server?serverTimezone=Asia/Seoul&characterEncoding=UTF-8"
            dynamicPropertyRegistry.add("spring.datasource.url" , { datasource })
            dynamicPropertyRegistry.add("spring.datasource.username", { USER_NAME })
            dynamicPropertyRegistry.add("spring.datasource.password", { PASSWORD })
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            container.stop()
        }
    }
}