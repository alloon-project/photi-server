package com.alloon.alloonserver.service.challenge.dto

import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.FeedComment

data class CreateChallengeFeedCommentDto(
    val comment: String,
) {

    fun toEntity(challengeMember: ChallengeMember, feed: Feed): FeedComment {
        return FeedComment(challengeMember = challengeMember, feed = feed, comment = comment)
    }
}