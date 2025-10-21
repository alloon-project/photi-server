package com.photi.core.domain.feedhistory.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class FeedHistory(
    feedId: Long,
) : BaseEntity() {

    @Id
    @Column(name = "feed_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false, unique = true)
    var feedId: Long = feedId
        protected set

    @Column(nullable = false)
    var likeCount: Int = 0
        protected set

    @Column(nullable = false)
    var commentCount: Int = 0
        protected set

    fun increaseLike() {
        likeCount += 1
    }

    fun decreaseLike() {
        if (likeCount > 0) {
            likeCount -= 1
        }
    }

    fun increaseComment() {
        commentCount += 1
    }

    fun decreaseComment() {
        if (commentCount > 0) {
            commentCount -= 1
        }
    }
}
