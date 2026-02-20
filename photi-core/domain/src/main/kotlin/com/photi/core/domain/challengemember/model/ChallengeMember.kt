package com.photi.core.domain.challengemember.model

import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeMember(
    userId: Long,
    challengeId: Long,
    isCreator: Boolean = true,
) : BaseEntity() {

    @Id
    @Column(name = "challenge_member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var userId: Long = userId
        protected set

    @Column(nullable = false)
    var challengeId: Long = challengeId
        protected set

    @Column(nullable = true, length = 16)
    var goal: String? = null
        protected set

    @Column(nullable = false)
    var isCreator: Boolean = isCreator
        protected set

    @Column(nullable = false, length = 15)
    @Enumerated(value = EnumType.STRING)
    var status: StatusType = StatusType.PROGRESS
        protected set

    fun registerGoal(dto: RegisterChallengePersonalGoalDto) {
        goal = dto.goal
    }

    fun deleted() {
        status = StatusType.DELETED
    }
}
