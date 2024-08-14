package com.alloon.alloonserver.api.controller.report.request

import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.REPORT_CATEGORY_TYPE_CHARACTER
import com.alloon.alloonserver.service.report.dto.ReportCreateServiceDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "신고 등록 요청 객체")
data class ReportCreateRequest(

    @Schema(description = "신고 대상 id", example = "1")
    @field:NotNull(message = "신고 대상 식별자는 필수 입력입니다.")
    val reportTargetId: Long,

    @Schema(description = "신고 항목", example = "CHALLENGE")
    @field:NotBlank(message = "신고 타입은 필수 입력입니다.")
    @field:Pattern(
        regexp = REPORT_CATEGORY_TYPE_CHARACTER,
        message = "신고 타입은 'CHALLENGE', 'CHALLENGE_MEMBER', 'FEED' 중 하나여야 됩니다."
    )
    val reportType: String,

    @Schema(description = "신고 카테고리 id", example = "1")
    @field:NotNull(message = "신고 카테고리 식별자는 필수 입력입니다.")
    val reportCategoryId: Int,

    @Schema(description = "신고 사유", example = "신고 사유입니다.")
    @field:Size(max = 120, message = "신고 사유는 0~120자만 가능합니다.")
    val reportReason: String?,
) {

    fun toServiceDto(): ReportCreateServiceDto {
        return ReportCreateServiceDto(reportTargetId, reportType, reportCategoryId, reportReason)
    }
}
