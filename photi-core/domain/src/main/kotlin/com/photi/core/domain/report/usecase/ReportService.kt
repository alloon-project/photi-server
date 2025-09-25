package com.photi.core.domain.report.usecase

import com.photi.core.domain.challenge.model.repository.ChallengeMemberRepository
import com.photi.core.domain.challenge.model.repository.ChallengeRepository
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.feed.model.repository.FeedRepository
import com.photi.core.domain.report.dto.CreateReportDto
import com.photi.core.domain.report.model.ReportCategoryType
import com.photi.core.domain.report.model.repository.ReportRepository
import com.photi.core.domain.user.model.repository.UserRepository
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
        userRepository.find(userId) ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)

        when (ReportCategoryType.valueOf(dto.category)) {
            ReportCategoryType.CHALLENGE -> challengeRepository.find(targetId)
                ?: throw CustomException(ExceptionCode.CHALLENGE_NOT_FOUND)

            ReportCategoryType.CHALLENGE_MEMBER -> challengeMemberRepository.find(targetId)
                ?: throw CustomException(ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND)

            ReportCategoryType.FEED -> feedRepository.find(targetId)
                ?: throw CustomException(ExceptionCode.FEED_NOT_FOUND)
        }
        reportRepository.save(dto.toReportEntity(userId, targetId))
    }
}
