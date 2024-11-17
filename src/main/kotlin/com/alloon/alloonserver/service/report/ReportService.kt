package com.alloon.alloonserver.service.report

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.ChallengeMemberRepository
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.report.ReportCategoryType
import com.alloon.alloonserver.domain.report.ReportCategoryType.*
import com.alloon.alloonserver.domain.report.ReportRepository
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.report.dto.CreateReportDto
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