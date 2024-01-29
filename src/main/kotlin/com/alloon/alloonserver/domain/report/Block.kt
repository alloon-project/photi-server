package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class Block(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    val blocker: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BasePermanentEntity() {
}