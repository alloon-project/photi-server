package com.photi.core.domain.userchallengehistory.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.core.domain.userchallengehistory.validator.UserChallengeHistoryValidator
import jakarta.persistence.*

@Entity
class UserChallengeHistory(

    @Id
    @Column(name = "user_challenge_history_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val userId: Long,

    @Column(nullable = false)
    var challengeCount: Int = 0,

    @Column(nullable = false)
    var endedChallengeCount: Int = 0,

    @Column(nullable = false)
    var feedCount: Int = 0,
) : BaseEntity() {

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
