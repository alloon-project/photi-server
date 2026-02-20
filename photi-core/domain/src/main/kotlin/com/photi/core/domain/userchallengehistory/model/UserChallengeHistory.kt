package com.photi.core.domain.userchallengehistory.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.core.domain.userchallengehistory.validator.UserChallengeHistoryValidator
import jakarta.persistence.*

@Entity
class UserChallengeHistory(
    userId: Long,
) : BaseEntity() {

    @Id
    @Column(name = "user_challenge_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false, unique = true)
    var userId: Long = userId
        protected set

    @Column(nullable = false)
    var challengeCount: Int = 1
        protected set

    @Column(nullable = false)
    var endedChallengeCount: Int = 0
        protected set

    @Column(nullable = false)
    var feedCount: Int = 0
        protected set

    fun increaseChallenge(userChallengeHistoryValidator: UserChallengeHistoryValidator) {
        userChallengeHistoryValidator.validateJoinChallengeCount(this)
        challengeCount += 1
    }

    fun decreaseChallenge() {
        if (challengeCount > 0) {
            challengeCount -= 1
        }
    }

    fun increaseFeed() {
        feedCount += 1
    }

    fun decreaseFeed() {
        if (feedCount > 0) {
            feedCount -= 1
        }
    }
}
