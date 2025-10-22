package com.photi.core.domain.feedhistory.service.command

import com.photi.core.domain.feedhistory.model.FeedHistory
import com.photi.core.domain.feedhistory.model.repository.FeedHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedHistoryCommandService(
    private val feedHistoryRepository: FeedHistoryRepository,
) {

    fun createFeedHistory(feedId: Long) {
        feedHistoryRepository.save(FeedHistory(feedId))
    }

    fun deleteFeedHistory(feedId: Long) {
        feedHistoryRepository.deleteById(feedId)
    }
}
