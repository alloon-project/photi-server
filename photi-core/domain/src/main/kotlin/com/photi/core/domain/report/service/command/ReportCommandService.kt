package com.photi.core.domain.report.service.command

import com.photi.core.domain.report.dto.CreateReportDto
import com.photi.core.domain.report.model.repository.ReportRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReportCommandService(
    private val reportRepository: ReportRepository,
) {

    fun createReport(dto: CreateReportDto, reporterId: Long, targetId: Long) {
        reportRepository.save(dto.toEntity(reporterId, targetId))
    }
}
