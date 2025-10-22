package com.photi.core.domain.feed.adapter

import com.photi.core.domain.challenge.port.ChallengeFeedPort
import com.photi.core.domain.feed.service.query.FeedQueryService
import com.photi.core.domain.feed.validator.FeedValidator
import com.photi.core.domain.report.port.ReportFeedPort
import org.springframework.stereotype.Component

@Component("FEED")
class FeedAdapter(
    private val feedValidator: FeedValidator,
    private val feedQueryService: FeedQueryService,
) : ReportFeedPort, ChallengeFeedPort {

    override fun validateExistsBy(targetId: Long) {
        feedValidator.validateExistsBy(targetId)
    }

    override fun existsFeedInChallengeBy(challengeId: Long) =
        feedQueryService.existsFeedInChallengeBy(challengeId)
}
