package com.photi.server.domain.user

import com.photi.server.domain.base.BaseEntity
import jakarta.persistence.*

@Entity
class UserRole(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    val role: Role,
    ) : BaseEntity() {
}