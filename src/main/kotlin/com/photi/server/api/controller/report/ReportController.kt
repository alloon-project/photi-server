package com.photi.server.api.controller.report

import com.photi.server.api.controller.report.request.CreateReportRequest
import com.photi.server.common.constant.ExceptionCode.*
import com.photi.server.common.response.ApiErrorResponses
import com.photi.server.common.response.StringSuccessResponse
import com.photi.server.common.util.UserUtility
import com.photi.server.config.SwaggerConfig.Companion.ACCESS_TOKEN_KEY
import com.photi.server.service.report.ReportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Validated
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report", description = "신고 API")
class ReportController(
    private val reportService: ReportService,
) {

    @PostMapping("/{targetId}")
    @Operation(summary = "신고 등록", security = [SecurityRequirement(name = ACCESS_TOKEN_KEY)])
    @ApiResponse(responseCode = "201")
    @ApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED, USER_NOT_FOUND, CHALLENGE_NOT_FOUND, CHALLENGE_MEMBER_NOT_FOUND, FEED_NOT_FOUND])
    fun createReport(
        principal: Principal,
        @PathVariable @Parameter(description = "신고 타겟 id", example = "1") targetId: Long,
        @RequestBody @Valid request: CreateReportRequest,
    ): ResponseEntity<StringSuccessResponse> {
        reportService.createReport(
            UserUtility.getUserId(principal),
            targetId,
            request.toServiceDto()
        )

        return ResponseEntity.status(CREATED).body(StringSuccessResponse("신고 등록이 완료되었습니다."))
    }
}