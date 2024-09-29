package com.alloon.alloonserver.service.challenge

import com.alloon.alloonserver.api.controller.challenge.response.FindChallengesResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.*
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.challenge.dto.*
import com.alloon.alloonserver.service.s3.FolderType.CHALLENGES
import com.alloon.alloonserver.service.s3.FolderType.FEEDS
import com.alloon.alloonserver.service.s3.S3Service
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class ChallengeService(
    private val challengeRepository: ChallengeRepository,
    private val challengeMemberRepository: ChallengeMemberRepository,
    private val userRepository: UserRepository,
    private val feedRepository: FeedRepository,
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
        val challenge = validateChallenge(challengeId)
        return FindChallengeInfoDto.of(challenge)
    }

    @Transactional
    fun updateChallengeMemberGoal(
        userId: Long,
        challengeId: Long,
        dto: UpdateChallengeMemberGoalDto
    ) {
        val challengeMember = validateChallengeMember(userId, challengeId)

        challengeMember.updateGoal(dto.goal)
    }

    fun findChallengeMembers(userId: Long, challengeId: Long): List<FindChallengeMembersDto> {
        return challengeMemberRepository.findAllByChallengeId(userId, challengeId)
    }

    fun findAllChallenges(pageable: Pageable): Slice<FindChallengesResponse> {
        return challengeRepository.findAllOrderByStartDate(pageable)
            .map { FindChallengesResponse.of(it) }
    }

    @Transactional
    fun findChallenge(challengeId: Long): FindChallengeDto {
        val challenge = validateChallenge(challengeId)
        val memberImages = challengeMemberRepository.findImagesByChallengeId(challengeId)
        challenge.updateVisitCnt()

        return FindChallengeDto.of(challenge, memberImages)
    }

    @Transactional
    fun updateChallenge(
        userId: Long,
        challengeId: Long,
        dto: UpdateChallengeDto,
        imageFile: MultipartFile
    ) {
        val challenge = validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        validateChallengeCreator(challengeMember)

        s3Service.deleteImage(challenge.imageUrl, CHALLENGES)
        val fileName = s3Service.uploadImage(imageFile, CHALLENGES)
        val imageUrl = s3Service.getImageUrl(fileName)

        dto.updateChallenge(challenge, imageUrl)
    }

    @Transactional
    fun deleteChallenge(userId: Long, challengeId: Long) {
        val challenge = validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)

        challengeMemberRepository.delete(challengeMember)

        if (challenge.currentMemberCnt == 1) {
            challengeRepository.deleteById(challengeId)
            s3Service.deleteImage(challenge.imageUrl, CHALLENGES)
        } else {
            challenge.decreaseCurrentMemberCnt()
        }
    }

    @Transactional
    fun createChallengeFeed(userId: Long, challengeId: Long, imageFile: MultipartFile) {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)
        val challenge = validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        validateChallengeMemberFeed(challengeMember)

        val fileName = s3Service.uploadImage(imageFile, FEEDS, challengeId)
        val imageUrl = s3Service.getImageUrl(fileName)
        val feed = Feed(
            challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl
        )

        feedRepository.save(feed)
        user.updateFeedCnt()
    }

    private fun validateChallenge(challengeId: Long): Challenge {
        return challengeRepository.findInfoById(challengeId)
            ?: throw CustomException(CHALLENGE_NOT_FOUND)
    }

    private fun validateChallengeMember(userId: Long, challengeId: Long): ChallengeMember {
        return challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
            ?: throw CustomException(CHALLENGE_MEMBER_NOT_FOUND)
    }

    private fun validateChallengeCreator(challengeMember: ChallengeMember) {
        if (!challengeMember.isCreator) {
            throw CustomException(CHALLENGE_CREATOR_FORBIDDEN)
        }
    }

    private fun validateChallengeMemberFeed(challengeMember: ChallengeMember) {
        val startOfDay = LocalDate.now().atStartOfDay()
        val endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
        val isFeedCreated = feedRepository.existsByChallengeMemberAndCreateDateTimeBetween(
            challengeMember,
            startOfDay,
            endOfDay
        )

        if (isFeedCreated) {
            throw CustomException(EXISTING_FEED)
        }
    }
}