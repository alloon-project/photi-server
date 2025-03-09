package com.photi.server.domain.feed

import com.photi.server.domain.base.BasePermanentEntity
import com.photi.server.domain.challenge.ChallengeMember
import jakarta.persistence.*

@Entity
class FeedLike(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id", nullable = false)
    val challengeMember: ChallengeMember,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    val feed: Feed,
) : BasePermanentEntity() {
}