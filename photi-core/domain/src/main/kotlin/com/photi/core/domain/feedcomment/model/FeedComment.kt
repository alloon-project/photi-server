package com.photi.core.domain.feedcomment.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(indexes = [Index(name = "idx_feed_comment_feed_id", columnList = "feed_id")])
class FeedComment(
    userId: Long,
    challengeMemberId: Long,
    feedId: Long,
    comment: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id", nullable = false)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var userId: Long = userId
        protected set

    @Column(nullable = false)
    var challengeMemberId: Long = challengeMemberId
        protected set

    @Column(nullable = false)
    var feedId: Long = feedId
        protected set

    @Column(nullable = false, length = 300)
    var comment: String = comment
        protected set
}
