package com.photi.core.domain.feed.adapter

import com.photi.core.domain.feed.validator.FeedValidator
import com.photi.core.domain.report.port.FeedPort
import org.springframework.stereotype.Component

@Component("FEED")
class FeedAdapter(
    private val feedValidator: FeedValidator,
) : FeedPort {

    override fun validateExistsBy(targetId: Long) {
        feedValidator.validateExistsBy(targetId)
    }
}
