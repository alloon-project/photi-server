package com.photi.server.domain.report

import com.photi.server.domain.base.BaseEntity
import com.photi.server.domain.user.User
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Suspension(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suspend_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    val admin: User,

    @Column(nullable = false)
    val startDate: LocalDate,
    @Column(nullable = false)
    val endDate: LocalDate,
) : BaseEntity() {
}