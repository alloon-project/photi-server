package com.alloon.alloonserver.service.challenge

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.challenge.ChallengeMemberRepository
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.challenge.ChallengeTemplateImageRepository
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.challenge.dto.*
import com.alloon.alloonserver.service.s3.S3Service
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class ChallengeService(
    private val challengeRepository: ChallengeRepository,
    private val challengeMemberRepository: ChallengeMemberRepository,
    private val challengeTemplateImageRepository: ChallengeTemplateImageRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) {

    @Transactional
    fun createChallenge(userId: Long, dto: CreateChallengeDto): Challenge {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val challenge = Challenge.toEntity(dto)
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        challengeRepository.save(challenge)
        challengeMemberRepository.save(challengeMember)

        return challenge
    }

    fun getAllChallengeTemplateImages(now: LocalDateTime): List<String> {
        return challengeTemplateImageRepository.findAllImageUrl(now)
    }

    @Transactional
    fun uploadChallengeImage(userId: Long, file: MultipartFile?): String {
        return s3Service.uploadFile(file, "users/$userId/missions", UUID.randomUUID().toString())
    }

    fun findPopularChallenges(): List<FindPopularChallengesDto> {
        return challengeRepository.findPopular()
    }

    fun findChallengeInfo(challengeId: Long): FindChallengeInfoDto {
        val challenge = challengeRepository.findInfoById(challengeId) ?: throw CustomException(CHALLENGE_NOT_FOUND)
        return FindChallengeInfoDto.of(challenge)
    }

    @Transactional
    fun updateChallengeMemberGoal(userId: Long, challengeId: Long, dto: UpdateChallengeMemberGoalDto) {
        val challengeMember = challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
            ?: throw CustomException(CHALLENGE_MEMBER_NOT_FOUND)

        challengeMember.updateGoal(dto.goal)
    }

    fun findChallengeMembers(userId: Long, challengeId: Long): List<FindChallengeMembersDto> {
        return challengeMemberRepository.findAllByChallengeId(userId, challengeId)
    }
}