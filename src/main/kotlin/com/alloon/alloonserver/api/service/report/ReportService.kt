package com.alloon.alloonserver.api.service.report

import com.alloon.alloonserver.api.service.report.request.ReportCreateServiceRequest
import com.alloon.alloonserver.common.constant.ExceptionCode
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.RegexPatternConstants.Companion.REPORT_CATEGORY_TYPE_CHARACTER
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.mission.MissionMemberRepository
import com.alloon.alloonserver.domain.mission.MissionRepository
import com.alloon.alloonserver.domain.report.ReportCategoryRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportCategoryType.*
import com.alloon.alloonserver.domain.report.ReportRepository
import com.alloon.alloonserver.domain.user.UserRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
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

    /**
     * 신고 항목 설명 전체 조회
     * @param reportType 신고 항목 종류
     * @throws REPORT_TYPE_INVALID 400
     * @return 신고 항목 설명들
     */
    fun getAllReportCategoryDescription(
        @Pattern(regexp = REPORT_CATEGORY_TYPE_CHARACTER,
            message = "신고 타입은 'MISSION', 'MISSION_MEMBER', 'FEED' 중 하나여야 됩니다.")
        reportType: String): List<String> {
        return reportCategoryRepository.findAllDescription(ReportCategoryType.valueOf(reportType))
    }

    /**
     * 신고 접수
     * @param userId 회원 식별자
     * @param request 신고 생성 요청
     * @throws USER_NOT_FOUND 404
     * @throws REPORT_CATEGORY_NOT_FOUND 404
     * @throws MISSION_NOT_FOUND 404
     * @throws MISSION_MEMBER_NOT_FOUND 404
     * @throws FEED_NOT_FOUND 404
     */
    fun createReport(userId: Long, @Valid request: ReportCreateServiceRequest) {
        val reporter = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val reportCategory = reportCategoryRepository.find(request.reportCategoryId,
            ReportCategoryType.valueOf(request.reportType)) ?: throw CustomException(REPORT_CATEGORY_NOT_FOUND)

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