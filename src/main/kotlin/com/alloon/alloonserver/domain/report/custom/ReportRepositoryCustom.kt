package com.alloon.alloonserver.domain.report.custom

import com.alloon.alloonserver.domain.report.Report

interface ReportRepositoryCustom {

    fun find(id: Long): Report?
}