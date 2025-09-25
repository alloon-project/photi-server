package com.photi.core.domain.feed.model.repository

import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.feed.model.Feed
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface FeedRepository : JpaRepository<Feed, Long>, FeedCustomRepository {

    fun existsByChallengeMemberAndCreateDateTimeBetween(
        challengeMember: ChallengeMember,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime,
    ): Boolean

    fun findByIdAndChallengeMemberId(feedId: Long, challengeMemberId: Long?): Feed?

    fun existsByChallengeId(id: Long): Boolean
}
