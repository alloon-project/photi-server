package com.photi.server.domain.challenge

import com.photi.server.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Entity
class ChallengeHashtag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_hashtag_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    var challenge: Challenge? = null,

    @Column(nullable = false, length = 6)
    val hashtag: String,
) : BasePermanentEntity()
