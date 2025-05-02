package com.photi.server.service.challenge

import com.photi.server.api.controller.challenge.response.*
import com.photi.server.common.constant.ExceptionCode.*
import com.photi.server.common.constant.SortTypeConstants
import com.photi.server.common.response.CustomException
import com.photi.server.common.util.CodeUtility
import com.photi.server.domain.challenge.Challenge
import com.photi.server.domain.challenge.ChallengeMember
import com.photi.server.domain.challenge.ChallengeMemberRepository
import com.photi.server.domain.challenge.ChallengeRepository
import com.photi.server.domain.feed.*
import com.photi.server.domain.user.User
import com.photi.server.domain.user.UserRepository
import com.photi.server.service.challenge.dto.*
import com.photi.server.service.idempotency.IdempotencyKeyService
import com.photi.server.service.s3.FolderType.CHALLENGES
import com.photi.server.service.s3.FolderType.FEEDS
import com.photi.server.service.s3.S3Service
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
    private val feedLikeRepository: FeedLikeRepository,
    private val s3Service: S3Service,
    private val hashtagService: HashtagService,
    private val idempotencyKeyService: IdempotencyKeyService,
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
        hashtagService.addHashtags(challenge.hashtags.map { it.hashtag })

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
        return challengeRepository.findAllOrderByEndDate(pageable)
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
        hashtagService.updateHashtags(challenge.hashtags.map { it.hashtag })

        dto.updateChallenge(challenge, imageUrl)
    }

    @Transactional
    fun deleteChallenge(userId: Long, challengeId: Long) {
        val challenge = validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)

        challengeMember.updateStatus()
        challengeMember.user.decreaseChallengeCnt()

        if (challenge.currentMemberCnt == 1) {
            hashtagService.deleteHashtags(challenge.hashtags.map { it.hashtag })
            challenge.updateChallengeStatusDeleted()
            s3Service.deleteImage(challenge.imageUrl, CHALLENGES)
        } else {
            challenge.decreaseCurrentMemberCnt()
        }
    }

    @Transactional
    fun createChallengeFeed(
        userId: Long,
        challengeId: Long,
        imageFile: MultipartFile,
    ): FindChallengeFeedsDto {
        val user = validateUser(userId)
        val challenge = validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        validateChallengeMemberFeed(challengeMember)

        val fileName = s3Service.uploadImage(imageFile, FEEDS, challengeId)
        val imageUrl = s3Service.getImageUrl(fileName)
        val feed =
            Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl)

        feedRepository.save(feed)
        user.updateFeedCnt()

        return FindChallengeFeedsDto.of(feed, user, challenge)
    }

    @Transactional
    fun deleteChallengeFeed(userId: Long, challengeId: Long, feedId: Long) {
        val user = validateUser(userId)
        validateChallenge(challengeId)
        val challengeMemberId = validateChallengeMember(userId, challengeId).id
        val feed = feedRepository.findByIdAndChallengeMemberId(feedId, challengeMemberId)
            ?: throw CustomException(FEED_NOT_FOUND)
        val feedComments = feedCommentRepository.findAllByFeedId(feedId)
        val feedLikes = feedLikeRepository.findAllByFeedId(feedId)

        feedCommentRepository.deleteAllInBatch(feedComments)
        feedLikeRepository.deleteAllInBatch(feedLikes)
        feedRepository.delete(feed)
        s3Service.deleteImage(feed.imageUrl, FEEDS, challengeId)
        user.decreaseFeedCnt()
    }

    fun findChallengeFeeds(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants,
    ): Slice<FindChallengeFeedsByDateResponse> {
        return feedRepository.findAllByChallengeId(userId, challengeId, pageable, sort)
            .map { FindChallengeFeedsByDateResponse.of(it) }
    }

    fun findChallengeFeedsV2(
        userId: Long,
        challengeId: Long,
        pageable: Pageable,
        sort: SortTypeConstants,
    ): Slice<FindChallengeFeedsResponse> {
        return feedRepository.findAllByChallengeIdV2(userId, challengeId, pageable, sort)
            .map { FindChallengeFeedsResponse.of(it) }
    }

    fun findChallengeFeedMemberCnt(challengeId: Long): FindChallengeFeedMemberCntDto {
        val feedMemberCnt = feedRepository.findFeedMemberCntByChallengeId(challengeId)
            ?: throw CustomException(CHALLENGE_NOT_FOUND)
        return FindChallengeFeedMemberCntDto.of(feedMemberCnt)
    }

    @Transactional
    fun createChallengeFeedComment(
        userId: Long,
        challengeId: Long,
        feedId: Long,
        dto: CreateChallengeFeedCommentDto
    ): Long? {
        validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        val feed = validateChallengeFeed(feedId)
        val feedComment = dto.toEntity(challengeMember, feed)

        feedCommentRepository.save(feedComment)
        feed.updateCommentCnt()

        return feedComment.id
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
        return feedRepository.findContentById(challengeId, feedId, userId) ?: throw CustomException(
            FEED_NOT_FOUND
        )
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

    fun findPopularChallengeHashtags(): Set<String> {
        return hashtagService.findPopularChallengeHashtags()
    }

    fun findChallengesByHashtag(
        hashtag: String?,
        pageable: Pageable
    ): Slice<FindChallengesResponse> {
        return if (hashtag == HASHTAG_ALL) {
            val hashtags = hashtagService.findPopularChallengeHashtags().map { it }
            challengeRepository.findAllByHashtag(popularHashtags = hashtags, pageable = pageable)
                .map { FindChallengesResponse.of(it) }
        } else {
            challengeRepository.findAllByHashtag(hashtag = hashtag, pageable = pageable)
                .map { FindChallengesResponse.of(it) }
        }
    }

    fun searchChallengeByName(
        challengeName: String,
        pageable: Pageable
    ): Slice<SearchChallengeByNameResponse> {
        return challengeRepository.searchByName(challengeName, pageable)
            .map { SearchChallengeByNameResponse.of(it) }
    }

    fun searchChallengeByHashtag(
        hashtag: String,
        pageable: Pageable
    ): Slice<SearchChallengeByHashtagResponse> {
        return challengeRepository.searchByHashtag(hashtag, pageable)
            .map { SearchChallengeByHashtagResponse.of(it) }
    }

    @Transactional
    fun joinPublicChallenge(
        userId: Long,
        challengeId: Long,
    ) {
        val user = validateUser(userId)
        val challenge = validateChallenge(challengeId)
        val challengeMember =
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        if (challengeMember != null) {
            throw CustomException(EXISTING_CHALLENGE_MEMBER)
        } else {
            if (user.challengeCnt >= CHALLENGE_LIMIT) {
                throw CustomException(CHALLENGE_LIMIT_EXCEED)
            }
            val newMember = ChallengeMember(user = user, challenge = challenge, isCreator = false)
            challengeMemberRepository.save(newMember)
            user.updateChallengeCnt()
            challenge.updateCurrentMemberCnt()
        }
    }

    @Transactional
    fun joinPrivateChallenge(
        userId: Long,
        challengeId: Long,
        dto: JoinPrivateChallengeDto
    ) {
        val user = validateUser(userId)
        val challenge = validateChallenge(challengeId)
        val challengeMember =
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        if (challengeMember != null) {
            throw CustomException(EXISTING_CHALLENGE_MEMBER)
        } else {
            if (user.challengeCnt >= CHALLENGE_LIMIT) {
                throw CustomException(CHALLENGE_LIMIT_EXCEED)
            }
            challenge.validateInvitationCode(dto.invitationCode)
            challengeMemberRepository.save(dto.toEntity(user, challenge))
            user.updateChallengeCnt()
            challenge.updateCurrentMemberCnt()
        }
    }

    @Transactional
    fun createChallengeFeedLike(userId: Long, challengeId: Long, feedId: Long) {
        validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        val feed = validateChallengeFeed(feedId)
        val feedLike = FeedLike(challengeMember = challengeMember, feed = feed)

        idempotencyKeyService.validateDuplicatedRequest(EXISTING_FEED_LIKE)

        try {
            feedLikeRepository.save(feedLike)
        } catch (e: Exception) {
            throw CustomException(EXISTING_FEED_LIKE)
        }
        feed.updateLikeCnt()
    }

    @Transactional
    fun deleteChallengeFeedLike(userId: Long, challengeId: Long, feedId: Long) {
        validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        val feed = validateChallengeFeed(feedId)
        val feedLike = feedLikeRepository.findByFeedIdAndChallengeMember(feedId, challengeMember)
            ?: throw CustomException(FEED_LIKE_NOT_FOUND)

        feedLikeRepository.delete(feedLike)
        feed.decreaseLikeCnt()
    }

    fun hasFeedByChallengeId(challengeId: Long): Boolean {
        validateChallenge(challengeId)
        return feedRepository.existsByChallengeId(challengeId)
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

    companion object {
        const val HASHTAG_ALL = "전체"
        const val CHALLENGE_LIMIT = 20
    }
}