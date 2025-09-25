package com.photi.core.domain.report.model

import com.photi.core.domain.common.model.BasePermanentEntity
import com.photi.core.domain.user.model.User
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
) : BasePermanentEntity()
