package com.alloon.alloonserver.api.service.report

import com.alloon.alloonserver.api.service.report.request.ReportCreateServiceRequest
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
     * 모든 신고 항목 리스트를 반환한다.
     *
     * @param reportType 신고 항목
     * @return 모든 신고 항목 리스트
     */
    fun getAllReportCategoryDescription(
        @Pattern(
            regexp = REPORT_CATEGORY_TYPE_CHARACTER,
            message = "신고 타입은 'MISSION', 'MISSION_MEMBER', 'FEED' 중 하나여야 됩니다."
        ) reportType: String
    ): List<String> {
        return reportCategoryRepository.findAllDescription(ReportCategoryType.valueOf(reportType))
    }

    /**
     * 신고를 새로 등록한다.
     *
     * @param userId 사용자 id
     * @param request 유효성 검사가 포함된 신고 등록 폼 데이터
     * @throws CustomException 사용자 정보를 찾을 수 없을 때 발생한다 ([USER_NOT_FOUND] 404)
     * @throws CustomException 신고 항목을 찾을 수 없을 때 발생한다 ([REPORT_CATEGORY_NOT_FOUND] 404)
     * @throws CustomException 챌린지 정보를 찾을 수 없을 때 발생한다 ([MISSION_NOT_FOUND] 404)
     * @throws CustomException 챌린지 파티원을 찾을 수 없을 때 발생한다 ([MISSION_MEMBER_NOT_FOUND] 404)
     * @throws CustomException 챌린지 피드를 찾을 수 없을 때 발생한다 ([FEED_NOT_FOUND] 404)
     */
    fun createReport(userId: Long, @Valid request: ReportCreateServiceRequest) {
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