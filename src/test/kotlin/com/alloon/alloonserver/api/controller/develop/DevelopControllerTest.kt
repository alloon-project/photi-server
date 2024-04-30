package com.alloon.alloonserver.api.controller.develop

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.service.develop.DevelopService
import com.alloon.alloonserver.api.service.develop.response.DevelopIsNeedForceUpdateResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DevelopControllerTest : RestDocsSupport() {

    private val developService = mock(DevelopService::class.java)

    @Override
    override fun initController(): Any {
        return DevelopController(developService)
    }

    @DisplayName("헬스 체크를 하면 200을 반환한다")
    @Test
    fun givenValid_whenHealthCheck_return200() {
        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/health")
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "develop/health",
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }

    @DisplayName("강제 업데이트 필요 여부 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenIsNeedForceUpdate_return200() {
        // given
        val version = "1.0.0"

        `when`(developService.needForceUpdate(anyString()))
            .thenReturn(DevelopIsNeedForceUpdateResponse(true))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/ver")
                .queryParam("version", version)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                MockMvcRestDocumentation.document(
                    "develop/need-force-update",
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("version")
                            .description("버전")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT)
                            .description("데이터"),
                        PayloadDocumentation.fieldWithPath("data.updateYn").type(JsonFieldType.BOOLEAN)
                            .description("강제 업데이트 필요 여부"),
                    )
                )
            )
    }

    @DisplayName("버전 미입력시 강제 업데이트 필요 여부 조회를 하면 400을 반환한다")
    @Test
    fun givenBlankVersion_whenIsNeedForceUpdate_return400() {
        // given
        val version = null

        `when`(developService.needForceUpdate(anyString()))
            .thenReturn(DevelopIsNeedForceUpdateResponse(true))

        // when & then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/ver")
                .queryParam("version", version)
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                MockMvcRestDocumentation.document(
                    "develop/need-force-update/version-field-required",
                    RequestDocumentation.queryParameters(
                        RequestDocumentation.parameterWithName("version")
                            .description("버전")
                    )
                )
            )
    }

}