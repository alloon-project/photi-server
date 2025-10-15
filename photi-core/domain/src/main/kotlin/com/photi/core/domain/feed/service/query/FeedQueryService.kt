package com.photi.core.domain.feed.service.query

import com.photi.core.domain.feed.model.SortType
import com.photi.core.domain.feed.model.repository.FeedRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class FeedQueryService(
    private val feedRepository: FeedRepository,
) {

    fun existsBy(id: Long) = feedRepository.existsById(id)

    fun existsTodayFeedBy(challengeMemberId: Long) =
        feedRepository.existsByChallengeMemberIdAndCreatedDateTimeBetween(
            challengeMemberId,
            getTodayStart(),
            getTodayEnd(),
        )

    fun existsFeedInChallengeBy(challengeId: Long) =
        feedRepository.existsByChallengeId(challengeId)

    fun getFeedBy(feedId: Long, challengeMemberId: Long) =
        feedRepository.findByIdAndChallengeMemberId(feedId, challengeMemberId)

    fun getFeedsBy(userId: Long, challengeId: Long, page: Int, size: Int, sort: SortType) =
        feedRepository.findFeedsById(userId, challengeId, PageRequest.of(page, size), sort)

    fun getFeedsByV2(userId: Long, challengeId: Long, page: Int, size: Int, sort: SortType) =
        feedRepository.findFeedsByIdV2(userId, challengeId, PageRequest.of(page, size), sort)

    fun getFeedBy(userId: Long, challengeId: Long, feedId: Long) =
        feedRepository.findFeedById(userId, challengeId, feedId)

    fun getTodayFeedMemberCountBy(challengeId: Long) =
        feedRepository.findTodayFeedMemberCountById(challengeId, getTodayStart(), getTodayEnd())

    private fun getTodayStart() = LocalDate.now().atStartOfDay()

    private fun getTodayEnd() = getTodayStart().plusDays(1)
}
