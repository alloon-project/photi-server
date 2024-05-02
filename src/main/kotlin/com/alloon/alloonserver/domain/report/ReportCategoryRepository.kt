package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.report.custom.ReportCategoryCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface ReportCategoryRepository : JpaRepository<ReportCategory, Long>, ReportCategoryCustomRepository {
}