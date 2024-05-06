package com.alloon.alloonserver.api.controller.report

import com.alloon.alloonserver.api.controller.report.request.ReportCreateRequest
import com.alloon.alloonserver.api.service.report.ReportService
import com.alloon.alloonserver.common.constant.SuccessCode.FOUND_MISSION_CATEGORIES
import com.alloon.alloonserver.common.constant.SuccessCode.REPORT_CREATED
import com.alloon.alloonserver.common.response.DefaultListResponse
import com.alloon.alloonserver.common.response.DefaultResponse
import com.alloon.alloonserver.common.util.UserUtility
import com.alloon.alloonserver.domain.report.ReportCategoryType.MISSION
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

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

    @PostMapping("/api/reports")
    fun reportMission(principal: Principal, @RequestBody @Valid request: ReportCreateRequest):
            ResponseEntity<DefaultResponse> {
        reportService.createReport(UserUtility.getUserId(principal), request.serviceRequest())

        return DefaultResponse.toResponseEntity(REPORT_CREATED)

    }
}