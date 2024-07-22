package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class MissionMember(

    @Id
    @Column(name = "mission_member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    val mission: Mission,

    @Column(nullable = false, length = 15)
    @Enumerated(value = EnumType.STRING)
    val status: MissionMemberStatus = MissionMemberStatus.PROGRESS,

    @Column(nullable = false)
    val isCreator: Boolean = true,
) : BasePermanentEntity() {
}