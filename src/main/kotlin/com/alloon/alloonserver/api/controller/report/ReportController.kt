package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.report.request.ReportCreateRequest
import com.alloon.alloonserver.api.service.report.ReportService
import com.alloon.alloonserver.api.service.report.request.ReportCreateServiceRequest
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_REPORT_CATEGORIES
import com.alloon.alloonserver.common.constant.SuccessCode.REPORT_CREATED
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.util.UserUtility
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
@Tag(name = "Report", description = "신고 API")
class ReportController(
    private val reportService: ReportService,
) {

    @GetMapping("/api/reports/category")
    @Operation(summary = "신고 항목 리스트 조회", description = "신고 항목별로 등록된 신고 리스트를 조회합니다.")
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "신고 항목 리스트 조회 성공")])
    fun getAllReportCategories(
        @RequestParam("type") @NotBlank(message = "신고 항목은 필수 입력입니다.")
        @Parameter(description = "신고 항목", example = "MISSION")
        reportType: String
    ): ResponseEntity<DefaultListResponse<String>> {
        val response = reportService.getAllReportCategoryDescription(reportType)

        return DefaultListResponse.toResponseEntity(FOUND_REPORT_CATEGORIES, response)
    }

    @PostMapping("/api/reports")
    @Operation(summary = "신고 등록")
    @ApiResponses(value = [ApiResponse(responseCode = "201", description = "신고 등록 성공")])
    fun reportMission(
        principal: Principal,
        @RequestBody @Valid @Schema(implementation = ReportCreateServiceRequest::class) request: ReportCreateRequest
    ): ResponseEntity<DefaultResponse> {
        reportService.createReport(UserUtility.getUserId(principal), request.serviceRequest())

        return DefaultResponse.toResponseEntity(REPORT_CREATED)
    }
}