package com.photi.core.domain.feedhistory.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class FeedHistory(

    @Id
    @Column(name = "feed_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val feedId: Long,

    @Column(nullable = false)
    var likeCount: Int = 0,

    @Column(nullable = false)
    var commentCount: Int = 0,
) : BaseEntity() {

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
