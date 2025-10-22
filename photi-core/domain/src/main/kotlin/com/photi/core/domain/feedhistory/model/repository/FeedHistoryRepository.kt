package com.photi.core.domain.feedhistory.model.repository

import com.photi.core.domain.feedhistory.model.FeedHistory
import org.springframework.data.jpa.repository.JpaRepository

interface FeedHistoryRepository : JpaRepository<FeedHistory, Long> {

    fun findByFeedId(feedId: Long): FeedHistory?
}
