package com.photi.core.domain.report.dto

import com.photi.core.domain.report.model.Report
import com.photi.core.domain.report.model.ReportCategoryType
import com.photi.core.domain.report.model.ReportReasonType

data class CreateReportDto(
    val category: String,
    val reason: String,
    val content: String?,
) {

    fun toReportEntity(reporterId: Long, targetId: Long) = Report(
        reporterId = reporterId,
        targetId = targetId,
        category = ReportCategoryType.valueOf(category),
        reason = ReportReasonType.valueOf(reason),
        content = content
    )
}
