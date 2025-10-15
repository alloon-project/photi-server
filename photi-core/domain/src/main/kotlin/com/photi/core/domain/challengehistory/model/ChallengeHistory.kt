package com.photi.core.domain.challengehistory.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeHistory(

    @Id
    @Column(name = "challenge_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val challengeId: Long,

    @Column(nullable = false)
    var challengeMemberCount: Int = 0,

    @Column(nullable = false)
    var visitCount: Int = 0,
) : BaseEntity() {

    fun increaseChallengeMember() {
        challengeMemberCount += 1
    }

    fun decreaseChallengeMember() {
        if (challengeMemberCount > 0) {
            challengeMemberCount -= 1
        }
    }

    fun increaseVisitCount() {
        visitCount += 1
    }
}
