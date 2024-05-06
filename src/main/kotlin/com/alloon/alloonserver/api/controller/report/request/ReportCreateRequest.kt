package com.alloon.alloonserver.api.controller.report.request

import com.alloon.alloonserver.api.service.report.request.ReportCreateServiceRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ReportCreateRequest(
    @field:NotNull(message = "신고 대상 식별자는 필수 입력입니다.")
    var reportTargetId: Long?,

    @field:NotBlank(message = "신고 타입은 필수 입력입니다.")
    var reportType: String?,

    @field:NotNull(message = "신고 카테고리 식별자는 필수 입력입니다.")
    var reportCategoryId: Int?,

    var reportReason: String?,
) {
    fun serviceRequest(): ReportCreateServiceRequest {
        return ReportCreateServiceRequest(reportTargetId!!, reportType!!, reportCategoryId!!, reportReason)
    }
}
