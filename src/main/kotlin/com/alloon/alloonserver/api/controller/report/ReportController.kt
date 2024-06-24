package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.report.request.ReportCreateRequest
import com.alloon.alloonserver.api.service.report.ReportService
import com.alloon.alloonserver.common.constant.SuccessCode.*
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.util.UserUtility
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
class ReportController(
    private val reportService: ReportService,
) {

    /**
     * 200 상태코드와 모든 신고 항목 리스트를 포함한 응답을 반환한다.
     *
     * @param reportType 신고 항목
     * @return [FOUND_REPORT_CATEGORIES] 및 모든 신고 항목 리스트를 포함한 응답
     */
    @GetMapping("/api/reports/category")
    fun getAllReportCategories(
        @RequestParam("type") @NotBlank(message = "신고 항목은 필수 입력입니다.") reportType: String
    ): ResponseEntity<DefaultListResponse<String>> {
        val response = reportService.getAllReportCategoryDescription(reportType)

        return DefaultListResponse.toResponseEntity(FOUND_REPORT_CATEGORIES, response)
    }

    /**
     * 201 상태코드를 포함한 응답을 반환한다.
     *
     * @param principal 사용자 인증 정보
     * @param request 신고 등록 폼 데이터
     * @return [REPORT_CREATED]를 포함한 응답
     */
    @PostMapping("/api/reports")
    fun reportMission(
        principal: Principal,
        @RequestBody @Valid request: ReportCreateRequest
    ): ResponseEntity<DefaultResponse> {
        reportService.createReport(UserUtility.getUserId(principal), request.serviceRequest())

        return DefaultResponse.toResponseEntity(REPORT_CREATED)
    }
}