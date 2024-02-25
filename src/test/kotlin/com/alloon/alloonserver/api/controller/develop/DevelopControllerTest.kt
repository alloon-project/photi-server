package com.alloon.alloonserver.api.controller.develop

import com.alloon.alloonserver.api.controller.WebMvcSupport
import com.alloon.alloonserver.api.service.develop.DevelopService
import com.alloon.alloonserver.common.constant.SuccessCode.SERVER_OK
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DevelopController::class)
abstract class DevelopControllerTest : WebMvcSupport() {

    @MockBean lateinit var developService: DevelopService

    @DisplayName("헬스 체크를 하면 200을 반환한다")
    @Test
    fun givenValid_whenHealthCheck_thenReturn200() {
        // given
        val serverName = "Alloon Test"
        `when`(developService.getServerName())
            .thenReturn(serverName)

        // when & then
        mockMvc.perform(
            get("/api/v1/health")
        ).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.responseCode")
                .value(SERVER_OK.name))
            .andExpect(jsonPath("$.responseMessage")
                .value(serverName + " " + SERVER_OK.message))
    }
}