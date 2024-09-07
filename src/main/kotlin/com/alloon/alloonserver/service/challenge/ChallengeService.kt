package com.alloon.alloonserver.service.challenge

import com.alloon.alloonserver.api.controller.challenge.response.FindChallengesResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.*
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.challenge.dto.*
import com.alloon.alloonserver.service.s3.FolderType.CHALLENGES
import com.alloon.alloonserver.service.s3.S3Service
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class ChallengeService(
    private val challengeRepository: ChallengeRepository,
    private val challengeMemberRepository: ChallengeMemberRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) {

    @Transactional
    fun createChallenge(
        userId: Long,
        dto: CreateChallengeDto,
        imageFile: MultipartFile
    ): CreateChallengeDto {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)
        val fileName = s3Service.uploadImage(imageFile, CHALLENGES)
        val imageUrl = s3Service.getImageUrl(fileName)

        val challenge = dto.toEntity(imageUrl)
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        challengeRepository.save(challenge)
        challengeMemberRepository.save(challengeMember)

        return CreateChallengeDto.of(challenge)
    }

    fun getChallengeExampleImages(): List<String> {
        return s3Service.getChallengeExampleImages()
    }

    fun findPopularChallenges(): List<FindChallengesDto> {
        return challengeRepository.findPopular()
    }

    fun findChallengeInfo(challengeId: Long): FindChallengeInfoDto {
        val challenge = challengeRepository.findInfoById(challengeId) ?: throw CustomException(
            CHALLENGE_NOT_FOUND
        )
        return FindChallengeInfoDto.of(challenge)
    }

    @Transactional
    fun updateChallengeMemberGoal(
        userId: Long,
        challengeId: Long,
        dto: UpdateChallengeMemberGoalDto
    ) {
        val challengeMember =
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
                ?: throw CustomException(CHALLENGE_MEMBER_NOT_FOUND)

        challengeMember.updateGoal(dto.goal)
    }

    fun findChallengeMembers(userId: Long, challengeId: Long): List<FindChallengeMembersDto> {
        return challengeMemberRepository.findAllByChallengeId(userId, challengeId)
    }

    fun findAllChallenges(pageable: Pageable): Slice<FindChallengesResponse> {
        return challengeRepository.findAllOrderByEndDate(pageable)
            .map { FindChallengesResponse.of(it) }
    }

    @Transactional
    fun findChallenge(challengeId: Long): FindChallengeDto {
        val challenge = challengeRepository.findInfoById(challengeId) ?: throw CustomException(
            CHALLENGE_NOT_FOUND
        )
        challenge.updateVisitCnt()

        return FindChallengeDto.of(challenge)
    }
}