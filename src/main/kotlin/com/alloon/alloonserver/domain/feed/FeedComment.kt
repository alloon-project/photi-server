package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.mission.MissionMember
import jakarta.persistence.*

@Entity
class FeedComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_comment_id")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_member_id", nullable = false)
    val missionMember: MissionMember,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    val feed: Feed,

    val comment: String,
) : BasePermanentEntity() {
}