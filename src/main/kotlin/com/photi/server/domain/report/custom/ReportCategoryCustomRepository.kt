package com.photi.server.domain.report.custom

import com.photi.server.domain.report.ReportCategory
import com.photi.server.domain.report.ReportCategoryType

interface ReportCategoryCustomRepository {

    fun findAllDescription(type: ReportCategoryType?): List<String>

    fun find(id: Long, type: ReportCategoryType?): ReportCategory?
}