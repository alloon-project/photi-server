package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Entity
class ChallengeRule(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_rule_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    var challenge: Challenge? = null,

    @Column(nullable = false, length = 30)
    val rule: String,
) : BasePermanentEntity() {
}