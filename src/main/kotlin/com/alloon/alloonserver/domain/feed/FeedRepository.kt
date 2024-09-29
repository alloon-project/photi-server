package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.feed.custom.FeedCustomRepository
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface FeedRepository : JpaRepository<Feed, Long>, FeedCustomRepository {

    fun existsByChallengeMemberAndCreateDateTimeBetween(
        challengeMember: ChallengeMember,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Boolean
}