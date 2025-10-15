package com.photi.core.domain.report.service

import com.photi.core.domain.report.service.command.ReportCommandService
import com.photi.core.domain.report.dto.CreateReportDto
import com.photi.core.domain.report.port.ReportCategoryPort
import com.photi.core.domain.report.port.ReportUserPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReportService(
    private val reportCommandService: ReportCommandService,
    private val userPort: ReportUserPort,
    private val categoryPorts: Map<String, ReportCategoryPort>,
) {

    @Transactional
    fun createReport(userId: Long, targetId: Long, dto: CreateReportDto) {
        userPort.getUserBy(userId)
        categoryPorts.getValue(dto.category).validateExistsBy(targetId)
        reportCommandService.createReport(dto, userId, targetId)
    }
}
