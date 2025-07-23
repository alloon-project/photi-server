package com.photi.server.domain.feed

import com.photi.server.domain.challenge.ChallengeMember
import com.photi.server.domain.feed.custom.FeedCustomRepository
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface FeedRepository : JpaRepository<Feed, Long>, FeedCustomRepository {

    fun existsByChallengeMemberAndCreateDateTimeBetween(
        challengeMember: ChallengeMember,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Boolean

    fun findByIdAndChallengeMemberId(feedId: Long, challengeMemberId: Long?): Feed?

    fun existsByChallengeId(id: Long): Boolean
}