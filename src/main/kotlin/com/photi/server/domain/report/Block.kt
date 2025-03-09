package com.photi.server.domain.report

import com.photi.server.domain.base.BasePermanentEntity
import com.photi.server.domain.user.User
import jakarta.persistence.*

@Entity
class Block(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", nullable = false)
    val blocker: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BasePermanentEntity() {
}