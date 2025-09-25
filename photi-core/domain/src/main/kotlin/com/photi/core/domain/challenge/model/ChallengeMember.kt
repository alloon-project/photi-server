package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BasePermanentEntity
import com.photi.core.domain.user.model.User
import jakarta.persistence.*

@Entity
class ChallengeMember(

    @Id
    @Column(name = "challenge_member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    val challenge: Challenge,

    @Column(length = 16)
    var goal: String? = null,

    @Column(nullable = false, length = 15)
    @Enumerated(value = EnumType.STRING)
    var status: ChallengeMemberStatus = ChallengeMemberStatus.PROGRESS,

    @Column(nullable = false)
    val isCreator: Boolean = true,
) : BasePermanentEntity() {

    fun updateGoal(goal: String) {
        this.goal = goal
    }

    fun updateStatus() {
        status = ChallengeMemberStatus.DELETED
    }
}
