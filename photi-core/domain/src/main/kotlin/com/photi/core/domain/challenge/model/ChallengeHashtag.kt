package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeHashtag(
    challenge: Challenge,
    hashtag: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_hashtag_id")
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    var challenge: Challenge = challenge
        protected set

    @Column(nullable = false, length = 6)
    var hashtag: String = hashtag
        protected set
}
