package com.alloon.alloonserver.api.service.report.request

import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.REPORT_CATEGORY_TYPE_CHARACTER
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.report.Report
import com.alloon.alloonserver.domain.report.ReportCategory
import com.alloon.alloonserver.domain.user.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "신고 등록 요청 객체")
data class ReportCreateServiceRequest(
    @Schema(description = "신고 대상 id", example = "1")
    var reportTargetId: Long,

    @field:Pattern(
        regexp = REPORT_CATEGORY_TYPE_CHARACTER,
        message = "신고 타입은 'MISSION', 'MISSION_MEMBER', 'FEED' 중 하나여야 됩니다."
    )
    @Schema(description = "신고 항목", example = "MISSION")
    var reportType: String,

    @Schema(description = "신고 카테고리 id", example = "1")
    var reportCategoryId: Int,

    @field:Size(max = 120, message = "신고 사유는 0~120자만 가능합니다.")
    @Schema(description = "신고 사유", example = "신고 사유입니다.")
    var reportReason: String?,
) {
    fun toEntity(
        reportCategory: ReportCategory,
        reporter: User,
        missionMember: MissionMember? = null,
        mission: Mission? = null,
        feed: Feed? = null,
    ): Report {
        return Report(
            reportCategory = reportCategory,
            reporter = reporter,
            mission = mission,
            missionMember = missionMember,
            feed = feed,
            reason = reportReason
        )
    }
}