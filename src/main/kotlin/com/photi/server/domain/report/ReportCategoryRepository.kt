package com.photi.server.domain.report

import com.photi.server.domain.report.custom.ReportCategoryCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ReportCategoryRepository : JpaRepository<ReportCategory, Long>,
    ReportCategoryCustomRepository {
}