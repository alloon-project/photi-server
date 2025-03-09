package com.photi.server.service.report

import com.photi.server.common.constant.ExceptionCode.*
import com.photi.server.common.response.CustomException
import com.photi.server.domain.challenge.ChallengeMemberRepository
import com.photi.server.domain.challenge.ChallengeRepository
import com.photi.server.domain.feed.FeedRepository
import com.photi.server.domain.report.ReportCategoryType
import com.photi.server.domain.report.ReportCategoryType.*
import com.photi.server.domain.report.ReportRepository
import com.photi.server.domain.user.UserRepository
import com.photi.server.service.report.dto.CreateReportDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReportService(
    private val reportRepository: ReportRepository,
    private val challengeRepository: ChallengeRepository,
    private val challengeMemberRepository: ChallengeMemberRepository,
    private val feedRepository: FeedRepository,
    private val userRepository: UserRepository,
) {

    @Transactional
    fun createReport(userId: Long, targetId: Long, dto: CreateReportDto) {
        userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        when (ReportCategoryType.valueOf(dto.category)) {
            CHALLENGE -> challengeRepository.find(targetId)
                ?: throw CustomException(CHALLENGE_NOT_FOUND)

            CHALLENGE_MEMBER -> challengeMemberRepository.find(targetId)
                ?: throw CustomException(CHALLENGE_MEMBER_NOT_FOUND)

            FEED -> feedRepository.find(targetId) ?: throw CustomException(FEED_NOT_FOUND)
        }
        reportRepository.save(dto.toReportEntity(userId, targetId))
    }
}