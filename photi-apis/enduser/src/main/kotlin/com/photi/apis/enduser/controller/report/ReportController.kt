package com.photi.apis.enduser.controller.report

import com.photi.apis.enduser.common.exception.ApiErrorResponses
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.report.request.CreateReportRequest
import com.photi.core.domain.common.consts.SwaggerConstants.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.report.usecase.ReportService
import com.photi.utils.UserUtil
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
    @ApiErrorResponses([ExceptionCode.TOKEN_UNAUTHENTICATED, ExceptionCode.TOKEN_UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, ExceptionCode.CHALLENGE_NOT_FOUND, ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND, ExceptionCode.FEED_NOT_FOUND])
    fun createReport(
        principal: Principal,
        @PathVariable @Parameter(description = "신고 타겟 id", example = "1") targetId: Long,
        @RequestBody @Valid request: CreateReportRequest,
    ): ResponseEntity<StringSuccessResponse> {
        reportService.createReport(
            UserUtil.getUserId(principal),
            targetId,
            request.toServiceDto()
        )
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("신고 등록이 완료되었습니다."))
    }
}
