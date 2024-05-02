package com.alloon.alloonserver.domain.report.custom

import com.alloon.alloonserver.domain.report.ReportCategoryType

interface ReportCategoryCustomRepository {

    fun findAllDescription(type: ReportCategoryType): List<String>
}