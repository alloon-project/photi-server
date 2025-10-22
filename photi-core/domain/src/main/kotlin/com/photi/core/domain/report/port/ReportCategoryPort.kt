package com.photi.core.domain.report.port

interface ReportCategoryPort {

    fun validateExistsBy(targetId: Long)
}
