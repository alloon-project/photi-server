package com.alloon.alloonserver.api.controller.develop

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.service.develop.DevelopService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DevelopControllerTest : RestDocsSupport() {

    private val developService = mock(DevelopService::class.java)

    override fun initController(): Any {
        return DevelopController(developService)
    }

    @DisplayName("헬스 체크를 하면 200을 반환한다")
    @Test
    fun givenValid_whenHealthCheck_return200() {
        // when & then
        mockMvc.perform(
            get("/api/health")
        ).andDo(print()).andExpect(status().isOk)
    }

    @DisplayName("강제 업데이트 필요 여부 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenIsNeedForceUpdate_return200() {
        // given
        val version = "1.0.0"

        `when`(developService.needForceUpdate(anyString()))
            .thenReturn(com.alloon.alloonserver.service.develop.response.DevelopIsNeedForceUpdateResponse(true))

        // when & then
        mockMvc.perform(
            get("/api/ver")
                .queryParam("version", version)
        ).andDo(print()).andExpect(status().isOk)
    }

    @DisplayName("버전 미입력시 강제 업데이트 필요 여부 조회를 하면 400을 반환한다")
    @Test
    fun givenBlankVersion_whenIsNeedForceUpdate_return400() {
        // given
        val version = null

        `when`(developService.needForceUpdate(anyString()))
            .thenReturn(com.alloon.alloonserver.service.develop.response.DevelopIsNeedForceUpdateResponse(true))

        // when & then
        mockMvc.perform(
            get("/api/ver")
                .queryParam("version", version)
        ).andDo(print()).andExpect(status().isBadRequest)
    }
}