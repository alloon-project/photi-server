package com.alloon.alloonserver.service.report.dto

import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.report.Report
import com.alloon.alloonserver.domain.report.ReportCategory
import com.alloon.alloonserver.domain.user.User

data class ReportCreateServiceDto(
    val reportTargetId: Long,
    val reportType: String,
    val reportCategoryId: Int,
    val reportReason: String?,
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