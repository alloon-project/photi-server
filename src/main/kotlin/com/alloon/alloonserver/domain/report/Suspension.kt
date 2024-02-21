package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.base.BaseEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDate

@Entity
class Suspension(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suspend_id")
    val id: BigInteger? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    val admin: User,

    @Column(nullable = false)
    val startedDatetime: LocalDate,
    @Column(nullable = false)
    val endedDatetime: LocalDate,
) : BaseEntity() {
}