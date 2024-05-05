package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.service.report.ReportService
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MISSION_CATEGORIES
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.domain.report.ReportCategoryType.MISSION
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class ReportController(
    private val reportService: ReportService,
) {

    @GetMapping("/api/reports/missions/category")
    fun getAllMissionReportCategories(): ResponseEntity<DefaultListResponse<String>> {
        val response = reportService.getAllReportCategoryDescription(MISSION)

        return DefaultListResponse.toResponseEntity(FOUND_MISSION_CATEGORIES, response)
    }
}