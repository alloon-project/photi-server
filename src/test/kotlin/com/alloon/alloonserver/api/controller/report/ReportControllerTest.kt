package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.report.request.ReportCreateRequest
import com.alloon.alloonserver.api.service.report.ReportService
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportCategoryType.FEED
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReportControllerTest : RestDocsSupport() {

    private val reportService = mock(ReportService::class.java)

    override fun initController(): Any {
        return ReportController(reportService)
    }

    @ParameterizedTest(name = "[{index}] {0} 신고 항목 종류 전체 조회를 하면 200을 반환한다")
    @EnumSource(ReportCategoryType::class)
    @DisplayName("신고 항목 전체 조회를 하면 200을 반환한다")
    fun givenEnum_whenGetAllReportCategories_thenReturn200(reportType: ReportCategoryType) {
        // when & then
        mockMvc.perform(
            get("/api/reports/category")
                .queryParam("type", reportType.name)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isOk)
            .andDo(
                document(
                    "report/get-all-report-categories",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    queryParameters(
                        parameterWithName("type").description("신고 항목 종류('MISSION', 'MISSION_MEMBER', 'FEED')")
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

    @DisplayName("신고 항목 종류 미입력시 신고 항목 전체 조회를 하면 400을 반환한다")
    @Test
    fun givenBlankReportType_whenGetAllReportCategories_thenReturn400() {
        // given
        val reportType = null

        // when & then
        mockMvc.perform(
            get("/api/reports/category")
                .queryParam("type", reportType)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "report/get-all-report-categories/report-type-field-required",
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    queryParameters(
                        parameterWithName("type").description("신고 항목 종류('MISSION', 'MISSION_MEMBER', 'FEED')")
                    )
                )
            )
    }

    @DisplayName("신고 등록을 하면 201을 반환한다")
    @Test
    fun givenValid_whenCreateReport_thenReturn201() {
        // given
        val request = createReportCreateRequest()

        // when & then
        mockMvc.perform(
            post("/api/reports")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isCreated)
            .andDo(
                document(
                    "report/create-report",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("reportTargetId").type(JsonFieldType.NUMBER)
                            .description("신고 대상자 식별자"),
                        fieldWithPath("reportType").type(JsonFieldType.STRING)
                            .description("신고 항목 종류('MISSION', 'MISSION_MEMBER', 'FEED')"),
                        fieldWithPath("reportCategoryId").type(JsonFieldType.NUMBER)
                            .description("신고 카테고리 식별자"),
                        fieldWithPath("reportReason").type(JsonFieldType.STRING).optional()
                            .description("신고 사유")
                    ),
                    responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING)
                            .description("코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                            .description("메세지")
                    )
                )
            )
    }

    @DisplayName("신고 대상 식별자 미입력시 신고 등록을 하면 400을 반환한다")
    @Test
    fun givenBlankReportTargetId_whenCreateReport_thenReturn400() {
        // given
        val request = createReportCreateRequest()
        request.reportTargetId = null

        // when & then
        mockMvc.perform(
            post("/api/reports")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "report/create-report/report-type-id-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("reportTargetId").type(JsonFieldType.NUMBER).optional()
                            .description("신고 대상자 식별자"),
                        fieldWithPath("reportType").type(JsonFieldType.STRING)
                            .description("신고 항목 종류('MISSION', 'MISSION_MEMBER', 'FEED')"),
                        fieldWithPath("reportCategoryId").type(JsonFieldType.NUMBER)
                            .description("신고 카테고리 식별자"),
                        fieldWithPath("reportReason").type(JsonFieldType.STRING).optional()
                            .description("신고 사유")
                    )
                )
            )
    }

    @DisplayName("신고 타입 미입력시 신고 등록을 하면 400을 반환한다")
    @Test
    fun givenBlankReportTarget_whenCreateReport_thenReturn400() {
        // given
        val request = createReportCreateRequest()
        request.reportType = ""

        // when & then
        mockMvc.perform(
            post("/api/reports")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "report/create-report/report-type-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("reportTargetId").type(JsonFieldType.NUMBER)
                            .description("신고 대상자 식별자"),
                        fieldWithPath("reportType").type(JsonFieldType.STRING)
                            .description("신고 항목 종류('MISSION', 'MISSION_MEMBER', 'FEED')"),
                        fieldWithPath("reportCategoryId").type(JsonFieldType.NUMBER)
                            .description("신고 카테고리 식별자"),
                        fieldWithPath("reportReason").type(JsonFieldType.STRING).optional()
                            .description("신고 사유")
                    ),
                )
            )
    }

    @DisplayName("신고 카테고리 미입력시 신고 등록을 하면 400을 반환한다")
    @Test
    fun givenBlankReportCategoryId_whenCreateReport_thenReturn400() {
        // given
        val request = createReportCreateRequest()
        request.reportCategoryId = null

        // when & then
        mockMvc.perform(
            post("/api/reports")
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andDo(print())
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "report/create-report/report-category-id-field-required",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName(AUTHORIZATION).description("액세스 토큰")
                    ),
                    requestFields(
                        fieldWithPath("reportTargetId").type(JsonFieldType.NUMBER)
                            .description("신고 대상자 식별자"),
                        fieldWithPath("reportType").type(JsonFieldType.STRING)
                            .description("신고 항목 종류('MISSION', 'MISSION_MEMBER', 'FEED')"),
                        fieldWithPath("reportCategoryId").type(JsonFieldType.NUMBER).optional()
                            .description("신고 카테고리 식별자"),
                        fieldWithPath("reportReason").type(JsonFieldType.STRING).optional()
                            .description("신고 사유")
                    ),
                )
            )
    }

    private fun createReportCreateRequest(): ReportCreateRequest {
        return ReportCreateRequest(1L, FEED.name, 1, "신고 사유")
    }
}