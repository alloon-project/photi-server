package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.service.report.ReportService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReportControllerTest : RestDocsSupport() {

    private val reportService = mock(ReportService::class.java)

    override fun initController(): Any {
        return ReportController(reportService)
    }

    @DisplayName("신고 항목 전체 조회를 하면 200을 반환한다")
    @Test
    fun givenValid_whenGetAllMissionReportCategories_thenReturn200() {
        // when & then
        mockMvc.perform(
            get("/api/v1/reports/missions/category")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "report/get-all-mission-report-categories",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY)
                            .description("데이터")
                    )
                )
            )
    }
}