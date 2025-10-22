package com.photi.core.domain.feed.validator

import com.photi.core.domain.feed.exception.FeedException
import com.photi.core.domain.feed.service.query.FeedQueryService
import org.springframework.stereotype.Component

@Component
class FeedValidator(
    private val feedQueryService: FeedQueryService,
) {

    fun validateExistsBy(targetId: Long) {
        if (!feedQueryService.existsBy(targetId)) {
            throw FeedException.NotFoundFeedException()
        }
    }

    fun validateExistsTodayFeedBy(challengeMemberId: Long) {
        if (feedQueryService.existsTodayFeedBy(challengeMemberId)) {
            throw FeedException.ExistsFeedException()
        }
    }
}
