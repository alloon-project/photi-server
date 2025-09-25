package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BasePermanentEntity
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
