package com.alloon.alloonserver.api.controller

import com.alloon.alloonserver.config.auth.CustomAuthenticationEntryPoint
import com.alloon.alloonserver.config.auth.JwtProvider
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
abstract class WebMvcSupport {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @MockBean lateinit var customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
    @MockBean lateinit var jwtProvider: JwtProvider
}