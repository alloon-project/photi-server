package com.photi.core.domain.feed.model

import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.common.model.BaseEntity
import com.photi.core.domain.feed.port.FeedFeedCommentPort
import com.photi.core.domain.feed.port.FeedFeedHistoryPort
import com.photi.core.domain.feed.port.FeedFeedLikePort
import com.photi.core.domain.feed.port.FeedS3Port
import jakarta.persistence.*

@Entity
@Table(
    indexes = [
        Index(columnList = "challenge_id, created_date_time"),
    ]
)
class Feed(
    userId: Long,
    challengeMemberId: Long,
    challengeId: Long,
    imageUrl: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id", nullable = false)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var userId: Long = userId
        protected set

    @Column(nullable = false)
    var challengeMemberId: Long = challengeMemberId
        protected set

    @Column(nullable = false)
    var challengeId: Long = challengeId
        protected set

    @Column(nullable = false, length = 500)
    var imageUrl: String = imageUrl
        protected set

    fun delete(
        s3Port: FeedS3Port,
        feedLikePort: FeedFeedLikePort,
        feedCommentPort: FeedFeedCommentPort,
        feedHistoryPort: FeedFeedHistoryPort,
    ) {
        deleteImage(s3Port)
        feedLikePort.deleteFeedLikes(id!!)
        feedCommentPort.deleteFeedComments(id!!)
        feedHistoryPort.deleteFeedHistory(id!!)
    }

    private fun deleteImage(s3Port: FeedS3Port) {
        s3Port.deleteImage(imageUrl, DirectoryType.FEEDS)
    }
}
