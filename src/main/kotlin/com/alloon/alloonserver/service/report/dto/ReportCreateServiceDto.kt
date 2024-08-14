package com.alloon.alloonserver.service.report.dto

import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.feed.Feed
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
        challengeMember: ChallengeMember? = null,
        challenge: Challenge? = null,
        feed: Feed? = null,
    ): Report {
        return Report(
            reportCategory = reportCategory,
            reporter = reporter,
            challenge = challenge,
            challengeMember = challengeMember,
            feed = feed,
            reason = reportReason
        )
    }
}