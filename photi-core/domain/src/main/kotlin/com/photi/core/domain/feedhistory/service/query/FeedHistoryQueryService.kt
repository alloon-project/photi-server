package com.photi.core.domain.feedhistory.service.query

import com.photi.core.domain.feedhistory.model.repository.FeedHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedHistoryQueryService(
    private val feedHistoryRepository: FeedHistoryRepository,
) {

    fun getFeedHistoryBy(feedId: Long) = feedHistoryRepository.findByFeedId(feedId)
}
