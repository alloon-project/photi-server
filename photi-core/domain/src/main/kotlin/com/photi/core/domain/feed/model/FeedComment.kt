package com.photi.core.domain.feed.model

import com.photi.core.domain.challenge.model.ChallengeMember
import com.photi.core.domain.common.model.BasePermanentEntity
import jakarta.persistence.*

@Entity
class FeedComment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id", nullable = false)
    val challengeMember: ChallengeMember,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    val feed: Feed,

    @Column(nullable = false)
    val comment: String,
) : BasePermanentEntity()
