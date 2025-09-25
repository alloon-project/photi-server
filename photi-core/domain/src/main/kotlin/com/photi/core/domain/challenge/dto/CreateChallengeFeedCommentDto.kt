package com.photi.core.domain.challenge.dto

import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.feed.model.Feed
import com.photi.core.domain.feed.model.FeedComment

data class CreateChallengeFeedCommentDto(
    val comment: String,
) {

    fun toEntity(challengeMember: ChallengeMember, feed: Feed) =
        FeedComment(challengeMember = challengeMember, feed = feed, comment = comment)
}
