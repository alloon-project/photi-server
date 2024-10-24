package com.alloon.alloonserver.service.challenge

import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeFeedCommentsResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengeFeedsByDateResponse
import com.alloon.alloonserver.api.controller.challenge.response.FindChallengesResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.SortTypeConstants
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.CodeUtility
import com.alloon.alloonserver.domain.challenge.*
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.FeedCommentRepository
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.user.User
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
    private val feedCommentRepository: FeedCommentRepository,
    private val s3Service: S3Service,
) {

    @Transactional
    fun createChallenge(
        userId: Long,
        dto: CreateChallengeDto,
        imageFile: MultipartFile
    ): CreateChallengeDto {
        val user = validateUser(userId)
        val fileName = s3Service.uploadImage(imageFile, CHALLENGES)
        val imageUrl = s3Service.getImageUrl(fileName)
        val invitationCode = CodeUtility.getInvitationCode()

        val challenge = dto.toEntity(imageUrl, invitationCode)
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        challengeRepository.save(challenge)
        challengeMemberRepository.save(challengeMember)

        return CreateChallengeDto.of(challenge)
    }

    fun getChallengeExampleImages(): List<String> {
        return s3Service.getChallengeExampleImages()
    }

    fun findPopularChallenges(): List<FindPopularChallengesDto> {
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
        val user = validateUser(userId)
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

    @Transactional
    fun deleteChallengeFeed(userId: Long, challengeId: Long, feedId: Long) {
        val user = validateUser(userId)
        validateChallenge(challengeId)
        val challengeMemberId = validateChallengeMember(userId, challengeId).id
        val feed = feedRepository.findByIdAndChallengeMemberId(feedId, challengeMemberId)
            ?: throw CustomException(FEED_NOT_FOUND)
        val feedComments = feedCommentRepository.findAllByFeedId(feedId)

        feedCommentRepository.deleteAllInBatch(feedComments)
        feedRepository.delete(feed)
        s3Service.deleteImage(feed.imageUrl, FEEDS, challengeId)
        user.decreaseFeedCnt()
    }

    fun findChallengeFeeds(
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants,
    ): Slice<FindChallengeFeedsByDateResponse> {
        return feedRepository.findAllByChallengeId(challengeId, pageable, sort)
            .map { (createdDate, feeds) ->
                FindChallengeFeedsByDateResponse.of(createdDate, feeds)
            }
    }

    @Transactional
    fun createChallengeFeedComment(
        userId: Long,
        challengeId: Long,
        feedId: Long,
        dto: CreateChallengeFeedCommentDto
    ) {
        validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        val feed = validateChallengeFeed(feedId)
        val feedComment = dto.toEntity(challengeMember, feed)

        feedCommentRepository.save(feedComment)
        feed.updateCommentCnt()
    }

    @Transactional
    fun deleteChallengeFeedComment(userId: Long, challengeId: Long, feedId: Long, commentId: Long) {
        validateChallenge(challengeId)
        val feed = validateChallengeFeed(feedId)
        validateChallengeMember(userId, challengeId)
        val feedComment = feedCommentRepository.findByCommentId(commentId)
            ?: throw CustomException(FEED_COMMENT_NOT_FOUND)

        feedCommentRepository.delete(feedComment)
        feed.decreaseCommentCnt()
    }

    fun findChallengeFeed(userId: Long, challengeId: Long, feedId: Long): FindChallengeFeedDto {
        validateChallenge(challengeId)
        validateChallengeMember(userId, challengeId)
        return feedRepository.findContentById(feedId) ?: throw CustomException(FEED_NOT_FOUND)
    }

    fun findChallengeFeedComments(
        feedId: Long,
        pageable: Pageable
    ): Slice<FindChallengeFeedCommentsResponse> {
        return feedCommentRepository.findAllByFeedId(feedId, pageable)
            .map { FindChallengeFeedCommentsResponse.of(it) }
    }

    fun findChallengeInvitationCode(
        userId: Long,
        challengeId: Long
    ): FindChallengeInvitationCodeDto {
        validateChallengeMember(userId, challengeId)
        return challengeRepository.findInvitationCodeById(challengeId)
            ?: throw CustomException(CHALLENGE_NOT_FOUND)
    }

    private fun validateUser(userId: Long): User {
        return userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)
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

    private fun validateChallengeFeed(feedId: Long): Feed {
        return feedRepository.findByFeedId(feedId) ?: throw CustomException(FEED_NOT_FOUND)
    }
}