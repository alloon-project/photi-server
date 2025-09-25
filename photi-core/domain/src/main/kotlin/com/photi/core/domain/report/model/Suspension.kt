package com.photi.core.domain.report.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.core.domain.user.model.User
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
) : BaseEntity()
