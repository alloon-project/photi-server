package com.photi.core.domain.report.model.repository

import com.photi.core.domain.report.model.Report
import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<Report, Long>
