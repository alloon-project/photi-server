package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.report.custom.ReportRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<Report, Long>, ReportRepositoryCustom {
}