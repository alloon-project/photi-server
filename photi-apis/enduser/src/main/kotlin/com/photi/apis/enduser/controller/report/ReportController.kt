package com.photi.apis.enduser.controller.report

import com.photi.apis.enduser.common.exception.annotation.*
import com.photi.apis.enduser.common.success.dto.StringSuccessResponse
import com.photi.apis.enduser.controller.report.request.CreateReportRequest
import com.photi.core.domain.challenge.exception.ChallengeErrorCode.CHALLENGE_NOT_FOUND
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode.CHALLENGE_MEMBER_NOT_FOUND
import com.photi.core.domain.common.consts.SwaggerKey.ACCESS_TOKEN_KEY
import com.photi.core.domain.common.exception.GlobalErrorCode.TOKEN_UNAUTHENTICATED
import com.photi.core.domain.common.exception.GlobalErrorCode.TOKEN_UNAUTHORIZED
import com.photi.core.domain.feed.exception.FeedErrorCode.FEED_NOT_FOUND
import com.photi.core.domain.report.service.ReportService
import com.photi.core.domain.user.exception.UserErrorCode.USER_NOT_FOUND
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
    @GlobalApiErrorResponses([TOKEN_UNAUTHENTICATED, TOKEN_UNAUTHORIZED])
    @UserApiErrorResponses([USER_NOT_FOUND])
    @ChallengeApiErrorResponses([CHALLENGE_NOT_FOUND])
    @ChallengeMemberApiErrorResponses([CHALLENGE_MEMBER_NOT_FOUND])
    @FeedApiErrorResponses([FEED_NOT_FOUND])
    fun createReport(
        principal: Principal,
        @PathVariable @Parameter(description = "신고 타겟 id", example = "1") targetId: Long,
        @RequestBody @Valid request: CreateReportRequest,
    ): ResponseEntity<StringSuccessResponse> {
        reportService.createReport(
            UserUtil.getUserId(principal),
            targetId,
            request.toServiceDto(),
        )
        return ResponseEntity.status(CREATED)
            .body(StringSuccessResponse("신고 등록이 완료되었습니다."))
    }
}
