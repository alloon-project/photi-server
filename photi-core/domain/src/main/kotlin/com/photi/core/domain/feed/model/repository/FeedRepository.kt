package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.feed.model.Feed
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface FeedRepository : JpaRepository<Feed, Long>, FeedCustomRepository {

    fun existsByChallengeMemberIdAndCreatedDateTimeBetween(
        challengeMemberId: Long,
        todayStart: LocalDateTime,
        todayEnd: LocalDateTime,
    ): Boolean

    fun existsByChallengeId(id: Long): Boolean

    fun findByIdAndChallengeMemberId(feedId: Long, challengeMemberId: Long): Feed?

    @Query("select count(*) from Feed f where f.challengeId = :challengeId and f.createdDateTime between :todayStart and :todayEnd")
    fun findTodayFeedMemberCountById(
        challengeId: Long,
        todayStart: LocalDateTime,
        todayEnd: LocalDateTime,
    ): Long?
}
