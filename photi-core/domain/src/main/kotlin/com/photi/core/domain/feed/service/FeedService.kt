package com.photi.core.domain.feed.service

import com.photi.core.domain.challenge.exception.ChallengeException
import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.feed.dto.FindImagePreSignedUrlDto
import com.photi.core.domain.feed.dto.FindTodayFeedMemberCountDto
import com.photi.core.domain.feed.dto.RegisterFeedDto
import com.photi.core.domain.feed.exception.FeedException
import com.photi.core.domain.feed.model.SortType
import com.photi.core.domain.feed.port.FeedChallengeMemberPort
import com.photi.core.domain.feed.port.FeedS3Port
import com.photi.core.domain.feed.port.FeedUserChallengeHistoryPort
import com.photi.core.domain.feed.service.command.FeedCommandService
import com.photi.core.domain.feed.service.query.FeedQueryService
import com.photi.core.domain.feed.validator.FeedValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedService(
    private val feedQueryService: FeedQueryService,
    private val feedCommandService: FeedCommandService,
    private val feedValidator: FeedValidator,
    private val challengeMemberPort: FeedChallengeMemberPort,
    private val userChallengeHistoryPort: FeedUserChallengeHistoryPort,
    private val s3Port: FeedS3Port,
) {

    fun findImagePreSignedUrl(dto: FindImagePreSignedUrlDto): String {
        // todo 파일 크기, 파일 형식 에러 지정, appversion과 같은 권한 (앱개발자)
        return s3Port.getPreSignedUrl(dto.imageName, DirectoryType.FEEDS)
    }

    @Transactional
    fun registerFeed(userId: Long, challengeId: Long, dto: RegisterFeedDto) {
        val challengeMemberId = getChallengeMemberIdBy(userId, challengeId)
        feedValidator.validateExistsTodayFeedBy(challengeMemberId)
        userChallengeHistoryPort.increaseFeed(userId)
        feedCommandService.createFeed(dto, userId, challengeMemberId, challengeId)
    }

    @Transactional
    fun deleteFeed(userId: Long, challengeId: Long, feedId: Long) {
        val challengeMemberId = getChallengeMemberIdBy(userId, challengeId)
        val feed = feedQueryService.getFeedBy(feedId, challengeMemberId)
            ?: throw FeedException.ForbiddenCreatorException()
        feed.deleteImage(s3Port)
        userChallengeHistoryPort.decreaseFeed(userId)
        feedCommandService.deleteFeed(feedId)
    }

    fun findFeeds(userId: Long, challengeId: Long, page: Int, size: Int, sort: SortType) =
        feedQueryService.getFeedsBy(userId, challengeId, page, size, sort)

    fun findFeedsV2(userId: Long, challengeId: Long, page: Int, size: Int, sort: SortType) =
        feedQueryService.getFeedsByV2(userId, challengeId, page, size, sort)

    fun findFeed(userId: Long, challengeId: Long, feedId: Long) =
        feedQueryService.getFeedBy(userId, challengeId, feedId)
            ?: throw FeedException.NotFoundFeedException()

    fun findTodayFeedMemberCount(challengeId: Long): FindTodayFeedMemberCountDto {
        val count = feedQueryService.getTodayFeedMemberCountBy(challengeId)
            ?: throw ChallengeException.NotFoundChallengeException()
        return FindTodayFeedMemberCountDto.of(count)
    }

    private fun getChallengeMemberIdBy(userId: Long, challengeId: Long) =
        challengeMemberPort.getChallengeMemberIdBy(userId, challengeId)
}
