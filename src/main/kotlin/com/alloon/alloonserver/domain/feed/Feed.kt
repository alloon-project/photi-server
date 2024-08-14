package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.challenge.Challenge
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import jakarta.persistence.*

@Entity
class Feed(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_member_id", nullable = false)
    val challengeMember: ChallengeMember,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    val challenge: Challenge,

    @Column(nullable = false, length = 500)
    val imageUrl: String,

    @Column(nullable = false)
    val likeCnt: Int = 0,
) : BasePermanentEntity() {
}