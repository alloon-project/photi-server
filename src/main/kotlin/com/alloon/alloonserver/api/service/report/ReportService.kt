package com.alloon.alloonserver.api.service.report

import com.alloon.alloonserver.domain.report.ReportCategoryRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReportService(
    private val reportCategoryRepository: ReportCategoryRepository,
) {

    /**
     * 신고 항목 설명 전체 조회
     * @param type 신고 항목 종류
     * @return 신고 항목 설명들
     */
    fun getAllReportCategoryDescription(type: ReportCategoryType): List<String> {
        return reportCategoryRepository.findAllDescription(type)
    }
}