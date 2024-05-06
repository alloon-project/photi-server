package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.feed.custom.FeedCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface FeedRepository : JpaRepository<Feed, Long>, FeedCustomRepository {
}