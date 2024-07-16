package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.report.request.ReportCreateRequest
import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.REPORT_CATEGORY_TYPE_CHARACTER
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_REPORT_CATEGORIES
import com.alloon.alloonserver.common.constant.SuccessCode.REPORT_CREATED
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.service.report.ReportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
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
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "신고 항목 리스트 조회 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
        ]
    )
    fun getAllReportCategories(
        @RequestParam("type") @NotBlank(message = "신고 항목은 필수 입력입니다.")
        @Pattern(
            regexp = REPORT_CATEGORY_TYPE_CHARACTER,
            message = "신고 타입은 'MISSION', 'MISSION_MEMBER', 'FEED' 중 하나여야 됩니다."
        )
        @Parameter(description = "신고 항목", example = "MISSION")
        reportType: String
    ): ResponseEntity<DefaultListResponse<String>> {
        val response = reportService.getAllReportCategoryDescription(reportType)

        return DefaultListResponse.toResponseEntity(FOUND_REPORT_CATEGORIES, response)
    }

    @PostMapping("/api/reports")
    @Operation(summary = "신고 등록")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "신고 등록 성공"),
            ApiResponse(responseCode = "401", description = "승인되지 않은 요청입니다. 다시 로그인 해주세요."),
            ApiResponse(responseCode = "403", description = "권한이 없는 요청입니다. 로그인 후에 다시 시도 해주세요."),
            ApiResponse(
                responseCode = "404",
                description = """
                1. 존재하지 않는 회원입니다.
                2. 존재하지 않는 신고 항목입니다.
                3. 존재하지 않는 미션입니다.
                4. 존재하지 않는 미션 멤버입니다.
                5. 존재하지 않는 피드입니다.
                """
            ),
        ]
    )
    fun reportMission(
        principal: Principal,
        @RequestBody @Valid request: ReportCreateRequest
    ): ResponseEntity<DefaultResponse> {
        reportService.createReport(UserUtility.getUserId(principal), request.toServiceDto())

        return DefaultResponse.toResponseEntity(REPORT_CREATED)
    }
}