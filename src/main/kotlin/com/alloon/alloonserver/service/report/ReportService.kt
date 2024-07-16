package com.alloon.alloonserver.service.report

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.mission.MissionMemberRepository
import com.alloon.alloonserver.domain.mission.MissionRepository
import com.alloon.alloonserver.domain.report.ReportCategoryRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportCategoryType.*
import com.alloon.alloonserver.domain.report.ReportRepository
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.report.dto.ReportCreateServiceDto
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Validated
@Service
@Transactional(readOnly = true)
class ReportService(
    private val reportCategoryRepository: ReportCategoryRepository,
    private val reportRepository: ReportRepository,
    private val missionRepository: MissionRepository,
    private val missionMemberRepository: MissionMemberRepository,
    private val feedRepository: FeedRepository,
    private val userRepository: UserRepository,
) {

    fun getAllReportCategoryDescription(reportType: String): List<String> {
        return reportCategoryRepository.findAllDescription(ReportCategoryType.valueOf(reportType))
    }

    fun createReport(userId: Long, @Valid request: ReportCreateServiceDto) {
        val reporter = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val reportCategory = reportCategoryRepository.find(
            request.reportCategoryId, ReportCategoryType.valueOf(request.reportType)
        ) ?: throw CustomException(REPORT_CATEGORY_NOT_FOUND)

        when (ReportCategoryType.valueOf(request.reportType)) {
            MISSION -> {
                val mission = missionRepository.find(request.reportTargetId) ?: throw CustomException(MISSION_NOT_FOUND)
                reportRepository.save(request.toEntity(reportCategory, reporter, mission = mission))
            }

            MISSION_MEMBER -> {
                val missionMember = missionMemberRepository.find(request.reportTargetId)
                    ?: throw CustomException(MISSION_MEMBER_NOT_FOUND)
                reportRepository.save(request.toEntity(reportCategory, reporter, missionMember = missionMember))
            }

            FEED -> {
                val feed = feedRepository.find(request.reportTargetId) ?: throw CustomException(FEED_NOT_FOUND)
                reportRepository.save(request.toEntity(reportCategory, reporter, feed = feed))
            }
        }
    }
}