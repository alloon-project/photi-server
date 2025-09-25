package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BasePermanentEntity
import com.photi.core.domain.common.model.ServiceStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
class Challenge(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    val id: Long? = null,

    @Column(nullable = false, length = 16)
    var name: String,

    @Column(nullable = false)
    val isPublic: Boolean,

    @Column(nullable = false, length = 120)
    var goal: String,

    @Column(nullable = false)
    var proveTime: LocalTime,

    @Column(nullable = false)
    var endDate: LocalDate,

    @Column(nullable = false, length = 500)
    var imageUrl: String,

    @Column(length = 5)
    val invitationCode: String,

    @Column(nullable = false)
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rules: MutableList<ChallengeRule> = mutableListOf(),

    @Column(nullable = false)
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    val hashtags: MutableList<ChallengeHashtag> = mutableListOf(),

    @Column(nullable = false)
    val startDate: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    var currentMemberCnt: Int = 1,

    @Column(nullable = false)
    var visitCnt: Int = 0,
) : BasePermanentEntity() {

    fun addChallengeRule(rule: ChallengeRule) {
        rules.add(rule)
        rule.challenge = this
    }

    fun addChallengeHashtag(hashtag: ChallengeHashtag) {
        hashtags.add(hashtag)
        hashtag.challenge = this
    }

    fun updateVisitCnt() {
        visitCnt += 1
    }

    fun decreaseCurrentMemberCnt() {
        currentMemberCnt -= 1
    }

    fun validateInvitationCode(invitationCode: String): Boolean {
        return invitationCode == this.invitationCode
    }

    fun updateChallengeStatusEnd() {
        serviceStatus = ServiceStatus.END
    }

    fun updateChallengeStatusDeleted() {
        serviceStatus = ServiceStatus.DELETED
    }

    fun updateCurrentMemberCnt() {
        currentMemberCnt += 1
    }
}
