package com.photi.core.domain.challengehistory.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeHistory(
    challengeId: Long,
) : BaseEntity() {

    @Id
    @Column(name = "challenge_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var challengeId: Long = challengeId
        protected set

    @Column(nullable = false)
    var challengeMemberCount: Int = 0
        protected set

    @Column(nullable = false)
    var visitCount: Int = 0
        protected set

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
