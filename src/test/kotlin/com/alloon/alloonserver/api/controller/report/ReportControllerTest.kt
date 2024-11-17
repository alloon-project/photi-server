package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.RestDocsSupport
import com.alloon.alloonserver.api.controller.report.request.CreateReportRequest
import com.alloon.alloonserver.service.report.ReportService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ReportControllerTest : RestDocsSupport() {

    private val reportService = mockk<ReportService>()

    override fun initController(): Any {
        return ReportController(reportService)
    }

    @DisplayName("신고 등록을 성공하면 201을 반환한다.")
    @Test
    fun givenValid_whenCreateReport_thenReturn201() {
        // given
        val request = CreateReportRequest("CHALLENGE", "DANGEROUS", "신고 내용")

        every { reportService.createReport(any(), any(), any()) } just Runs

        // when
        val resultActions = mockMvc.perform(
            post("/api/reports/{targetId}", 1)
                .header(AUTHORIZATION, "Bearer access-token")
                .principal(mockPrincipal)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )

        // then
        resultActions.andExpect(status().isCreated)
    }
}