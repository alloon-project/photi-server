package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class UserRole(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, length = 6)
    @Enumerated(EnumType.STRING)
    val role: Role,
    ) : BaseEntity() {
}