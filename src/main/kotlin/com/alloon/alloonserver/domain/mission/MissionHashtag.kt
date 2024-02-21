package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*
import java.math.BigInteger

@Entity
class MissionHashtag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_hashtag_id")
    val id: BigInteger? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    val mission: Mission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    val hashtag: Hashtag,
) : BasePermanentEntity() {
}