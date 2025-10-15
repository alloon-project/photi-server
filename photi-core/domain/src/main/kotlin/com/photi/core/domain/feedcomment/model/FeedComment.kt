package com.photi.core.domain.feedcomment.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class FeedComment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val challengeMemberId: Long,

    @Column(nullable = false)
    val feedId: Long,

    @Column(nullable = false)
    val comment: String,
) : BaseEntity()
