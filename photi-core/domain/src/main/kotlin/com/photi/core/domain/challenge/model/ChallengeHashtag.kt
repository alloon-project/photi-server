package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeHashtag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_hashtag_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    val challenge: Challenge,

    @Column(nullable = false, length = 6)
    val hashtag: String,
) : BaseEntity()
