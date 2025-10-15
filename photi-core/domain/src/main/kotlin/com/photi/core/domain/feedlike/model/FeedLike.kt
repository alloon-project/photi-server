package com.photi.core.domain.feedlike.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["challenge_member_id", "feed_id"]),
    ]
)
class FeedLike(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false)
    val challengeMemberId: Long,

    @Column(nullable = false)
    val feedId: Long,
) : BaseEntity()
