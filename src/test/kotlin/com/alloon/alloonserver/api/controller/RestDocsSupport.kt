package com.alloon.alloonserver.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.security.Principal

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsSupport {

    protected lateinit var mockMvc: MockMvc
    protected val objectMapper = ObjectMapper()
    protected val mockPrincipal = Mockito.mock(Principal::class.java)!!

    @BeforeEach
    fun setUp(provider: RestDocumentationContextProvider) {
        `when`(mockPrincipal.name).thenReturn("1")

        MockitoAnnotations.openMocks(this)

        objectMapper.registerModule(JavaTimeModule())

        mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .alwaysDo<StandaloneMockMvcBuilder>(print())
            .apply<StandaloneMockMvcBuilder>(documentationConfiguration(provider))
            .build()
    }

    protected abstract fun initController(): Any

    protected fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}