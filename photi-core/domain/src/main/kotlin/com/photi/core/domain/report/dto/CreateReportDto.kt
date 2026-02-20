package com.photi.core.domain.report.dto

import com.photi.core.domain.report.model.Report
import com.photi.core.domain.report.model.CategoryType
import com.photi.core.domain.report.model.ReasonType

data class CreateReportDto(
    val category: String,
    val reason: String,
    val content: String?,
) {

    fun toEntity(reporterId: Long, targetId: Long) = Report(
        reporterId,
        targetId,
        CategoryType.valueOf(category),
        ReasonType.valueOf(reason),
        content,
    )
}
