package com.photi.core.domain.feedlike.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        name = "uq_feed_like_challenge_member_id_feed_id",
        columnNames = ["challenge_member_id", "feed_id"],
    )]
)
class FeedLike(
    challengeMemberId: Long,
    feedId: Long,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_id", nullable = false)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var challengeMemberId: Long = challengeMemberId
        protected set

    @Column(nullable = false)
    var feedId: Long = feedId
        protected set
}
