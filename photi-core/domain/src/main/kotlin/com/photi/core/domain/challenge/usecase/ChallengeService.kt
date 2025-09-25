package com.photi.core.domain.challenge.usecase

import com.photi.core.domain.challenge.dto.*
import com.photi.core.domain.challenge.model.Challenge
import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.challenge.model.repository.ChallengeMemberRepository
import com.photi.core.domain.challenge.model.repository.ChallengeRepository
import com.photi.core.domain.common.SliceDto
import com.photi.core.domain.common.consts.SortTypeConstants
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.feed.model.Feed
import com.photi.core.domain.feed.model.FeedLike
import com.photi.core.domain.feed.model.repository.FeedCommentRepository
import com.photi.core.domain.feed.model.repository.FeedLikeRepository
import com.photi.core.domain.feed.model.repository.FeedRepository
import com.photi.core.domain.idempotencykey.usecase.IdempotencyKeyService
import com.photi.core.domain.s3.model.FolderType
import com.photi.core.domain.s3.usecase.S3Service
import com.photi.core.domain.user.model.User
import com.photi.core.domain.user.model.repository.UserRepository
import com.photi.utils.CodeUtil.getInvitationCode
import org.springframework.data.domain.PageRequest
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
        imageFile: MultipartFile,
    ): CreateChallengeDto {
        val user = validateUser(userId)
        user.validateChallengeCnt()
        val fileName = s3Service.uploadImage(imageFile, FolderType.CHALLENGES)
        val imageUrl = s3Service.getImageUrl(fileName)
        val invitationCode = getInvitationCode(dto.isPublic)

        val challenge = dto.toEntity(imageUrl, invitationCode)
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        user.updateChallengeCnt()
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

    fun findAllChallenges(page: Int, size: Int): SliceDto<FindChallengesDto> {
        val pageable = PageRequest.of(page, size)
        return challengeRepository.findAllOrderByEndDate(pageable)
    }

    @Transactional
    fun findChallenge(challengeId: Long): FindChallengeDto {
        val challenge = validateChallenge(challengeId)
        val memberImages = challengeMemberRepository.findImagesByChallengeId(challengeId)
        val creator = challengeMemberRepository.findCreatorByChallengeId(challengeId)
        challenge.updateVisitCnt()

        return FindChallengeDto.of(challenge, memberImages, creator)
    }

    @Transactional
    fun updateChallenge(
        userId: Long,
        challengeId: Long,
        dto: UpdateChallengeDto,
        imageFile: MultipartFile,
    ) {
        val challenge = validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        validateChallengeCreator(challengeMember)

        s3Service.deleteImage(challenge.imageUrl, FolderType.CHALLENGES)
        val fileName = s3Service.uploadImage(imageFile, FolderType.CHALLENGES)
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
            s3Service.deleteImage(challenge.imageUrl, FolderType.CHALLENGES)
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

        val fileName = s3Service.uploadImage(imageFile, FolderType.FEEDS, challengeId)
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
            ?: throw CustomException(ExceptionCode.FEED_CREATOR_FORBIDDEN)
        val feedComments = feedCommentRepository.findAllByFeedId(feedId)
        val feedLikes = feedLikeRepository.findAllByFeedId(feedId)

        feedCommentRepository.deleteAllInBatch(feedComments)
        feedLikeRepository.deleteAllInBatch(feedLikes)
        feedRepository.delete(feed)
        s3Service.deleteImage(feed.imageUrl, FolderType.FEEDS, challengeId)
        user.decreaseFeedCnt()
    }

    fun findChallengeFeeds(
        userId: Long,
        challengeId: Long,
        page: Int,
        size: Int,
        sort: SortTypeConstants,
    ): SliceDto<Triple<LocalDate, List<FindChallengeFeedsDto>, Int>> {
        val pageable = PageRequest.of(page, size)
        return feedRepository.findAllByChallengeId(userId, challengeId, pageable, sort)
    }

    fun findChallengeFeedsV2(
        userId: Long,
        challengeId: Long,
        page: Int,
        size: Int,
        sort: SortTypeConstants,
    ): SliceDto<FindChallengeFeedsDto> {
        val pageable = PageRequest.of(page, size)
        return feedRepository.findAllByChallengeIdV2(userId, challengeId, pageable, sort)
    }

    fun findChallengeFeedMemberCnt(challengeId: Long): FindChallengeFeedMemberCntDto {
        val feedMemberCnt = feedRepository.findFeedMemberCntByChallengeId(challengeId)
            ?: throw CustomException(ExceptionCode.CHALLENGE_NOT_FOUND)
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
            ?: throw CustomException(ExceptionCode.FEED_COMMENT_NOT_FOUND)

        feedCommentRepository.delete(feedComment)
        feed.decreaseCommentCnt()
    }

    fun findChallengeFeed(userId: Long, challengeId: Long, feedId: Long): FindChallengeFeedDto {
        validateChallenge(challengeId)
        validateChallengeMember(userId, challengeId)
        return feedRepository.findContentById(challengeId, feedId, userId)
            ?: throw CustomException(ExceptionCode.FEED_NOT_FOUND)
    }

    fun findChallengeFeedComments(
        feedId: Long,
        page: Int,
        size: Int,
    ): SliceDto<FindChallengeFeedCommentsDto> {
        val pageable = PageRequest.of(page, size)
        return feedCommentRepository.findAllByFeedId(feedId, pageable)
    }

    fun findChallengeInvitationCode(
        userId: Long,
        challengeId: Long,
    ): FindChallengeInvitationCodeDto {
        validateChallengeMember(userId, challengeId)
        return challengeRepository.findInvitationCodeById(challengeId)
            ?: throw CustomException(ExceptionCode.CHALLENGE_NOT_FOUND)
    }

    fun findPopularChallengeHashtags(): Set<String> {
        return hashtagService.findPopularChallengeHashtags()
    }

    fun findChallengesByHashtag(
        hashtag: String?,
        page: Int,
        size: Int,
    ): SliceDto<FindChallengesDto> {
        val pageable = PageRequest.of(page, size)
        return if (hashtag == HASHTAG_ALL) {
            val hashtags = hashtagService.findPopularChallengeHashtags().map { it }
            challengeRepository.findAllByHashtag(popularHashtags = hashtags, pageable = pageable)
        } else {
            challengeRepository.findAllByHashtag(hashtag = hashtag, pageable = pageable)
        }
    }

    fun searchChallengeByName(
        challengeName: String,
        page: Int,
        size: Int,
    ): SliceDto<SearchChallengeByNameDto> {
        val pageable = PageRequest.of(page, size)
        return challengeRepository.searchByName(challengeName, pageable)
    }

    fun searchChallengeByHashtag(
        hashtag: String,
        page: Int,
        size: Int,
    ): SliceDto<SearchChallengeByHashtagDto> {
        val pageable = PageRequest.of(page, size)
        return challengeRepository.searchByHashtag(hashtag, pageable)
    }

    @Transactional
    fun joinChallenge(
        userId: Long,
        challengeId: Long,
        dto: JoinChallengeDto,
    ) {
        val user = validateUser(userId)
        val challenge = validateChallenge(challengeId)
        val challengeMember =
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        validateExistingChallengeMember(challengeMember)
        user.validateChallengeCnt()

        val newMember = ChallengeMember(user = user, challenge = challenge, isCreator = false)
        challengeMemberRepository.save(newMember)
        user.updateChallengeCnt()
        challenge.updateCurrentMemberCnt()
        newMember.updateGoal(dto.goal)
    }

    fun isMatchInvitationCode(challengeId: Long, invitationCode: String): Boolean {
        val challenge = validateChallenge(challengeId)
        return challenge.validateInvitationCode(invitationCode)
    }

    @Transactional
    fun createChallengeFeedLike(userId: Long, challengeId: Long, feedId: Long) {
        validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        val feed = validateChallengeFeed(feedId)

        idempotencyKeyService.validateDuplicatedRequest(
            "like:$userId:$feedId",
            ExceptionCode.EXISTING_FEED_LIKE,
        )

        try {
            val feedLike = FeedLike(challengeMember = challengeMember, feed = feed)
            feedLikeRepository.save(feedLike)
            feed.updateLikeCnt()
        } catch (e: Exception) {
            throw CustomException(ExceptionCode.EXISTING_FEED_LIKE)
        }
    }

    @Transactional
    fun deleteChallengeFeedLike(userId: Long, challengeId: Long, feedId: Long) {
        validateChallenge(challengeId)
        val challengeMember = validateChallengeMember(userId, challengeId)
        val feed = validateChallengeFeed(feedId)
        val feedLike = feedLikeRepository.findByFeedIdAndChallengeMember(feedId, challengeMember)
            ?: throw CustomException(ExceptionCode.FEED_LIKE_NOT_FOUND)

        feedLikeRepository.delete(feedLike)
        feed.decreaseLikeCnt()
    }

    fun hasFeedByChallengeId(challengeId: Long): Boolean {
        validateChallenge(challengeId)
        return feedRepository.existsByChallengeId(challengeId)
    }

    private fun validateUser(userId: Long): User {
        return userRepository.find(userId) ?: throw CustomException(ExceptionCode.USER_NOT_FOUND)
    }

    private fun validateChallenge(challengeId: Long): Challenge {
        return challengeRepository.findInfoById(challengeId)
            ?: throw CustomException(ExceptionCode.CHALLENGE_NOT_FOUND)
    }

    private fun validateChallengeMember(userId: Long, challengeId: Long): ChallengeMember {
        return challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
            ?: throw CustomException(ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND)
    }

    private fun validateChallengeCreator(challengeMember: ChallengeMember) {
        if (!challengeMember.isCreator) {
            throw CustomException(ExceptionCode.CHALLENGE_CREATOR_FORBIDDEN)
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
            throw CustomException(ExceptionCode.EXISTING_FEED)
        }
    }

    private fun validateChallengeFeed(feedId: Long): Feed {
        return feedRepository.findByFeedId(feedId)
            ?: throw CustomException(ExceptionCode.FEED_NOT_FOUND)
    }

    private fun validateExistingChallengeMember(challengeMember: ChallengeMember?) {
        if (challengeMember != null) {
            throw CustomException(ExceptionCode.EXISTING_CHALLENGE_MEMBER)
        }
    }

    companion object {
        const val HASHTAG_ALL = "전체"
    }
}
