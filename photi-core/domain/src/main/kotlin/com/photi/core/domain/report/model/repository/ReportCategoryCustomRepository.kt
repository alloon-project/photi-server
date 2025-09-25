package com.photi.core.domain.report.model.repository

import com.photi.core.domain.report.model.ReportCategory
import com.photi.core.domain.report.model.ReportCategoryType

interface ReportCategoryCustomRepository {

    fun findAllDescription(type: ReportCategoryType?): List<String>

    fun find(id: Long, type: ReportCategoryType?): ReportCategory?
}
