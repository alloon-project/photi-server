package com.photi.core.domain.report.model.repository

import com.photi.core.domain.report.model.ReportCategory
import org.springframework.data.jpa.repository.JpaRepository

interface ReportCategoryRepository : JpaRepository<ReportCategory, Long>,
    ReportCategoryCustomRepository
