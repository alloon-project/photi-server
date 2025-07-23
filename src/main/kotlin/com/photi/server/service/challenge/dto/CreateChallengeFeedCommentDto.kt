package com.photi.server.service.challenge.dto

import com.photi.server.domain.challenge.ChallengeMember
import com.photi.server.domain.feed.Feed
import com.photi.server.domain.feed.FeedComment

data class CreateChallengeFeedCommentDto(
    val comment: String,
) {

    fun toEntity(challengeMember: ChallengeMember, feed: Feed): FeedComment {
        return FeedComment(challengeMember = challengeMember, feed = feed, comment = comment)
    }
}