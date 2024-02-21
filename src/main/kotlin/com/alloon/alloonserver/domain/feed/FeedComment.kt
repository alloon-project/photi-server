package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.mission.MissionMember
import jakarta.persistence.*
import java.math.BigInteger

@Entity
class FeedComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id")
    val id: BigInteger? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_member_id", nullable = false)
    val missionMember: MissionMember,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", nullable = false)
    val feed: Feed,

    @Column(nullable = false)
    val comment: String,
) : BasePermanentEntity() {
}