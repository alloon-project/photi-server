package com.alloon.alloonserver.service.report.dto

import com.alloon.alloonserver.domain.report.Report
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportReasonType

data class CreateReportDto(
    val category: String,
    val reason: String,
    val content: String?,
) {

    fun toReportEntity(reporterId: Long, targetId: Long): Report {
        return Report(
            reporterId = reporterId,
            targetId = targetId,
            category = ReportCategoryType.valueOf(category),
            reason = ReportReasonType.valueOf(reason),
            content = content
        )
    }
}