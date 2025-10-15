package com.photi.core.domain.challengemember.model

import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeMember(

    @Id
    @Column(name = "challenge_member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val challengeId: Long,

    @Column(nullable = true, length = 16)
    var goal: String? = null,

    @Column(nullable = false)
    val isCreator: Boolean = true,

    @Column(nullable = false, length = 15)
    @Enumerated(value = EnumType.STRING)
    var status: StatusType = StatusType.PROGRESS,
) : BaseEntity() {

    fun registerGoal(dto: RegisterChallengePersonalGoalDto) {
        goal = dto.goal
    }

    fun deleted() {
        status = StatusType.DELETED
    }
}
