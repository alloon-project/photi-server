package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Table(name = "users")
@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    val contact: Contact,

    @Column(nullable = false, length = 15)
    val username: String,
    @Column(nullable = false)
    val password: String,
    val imageUrl: String? = null,

    @Column(nullable = false)
    val isTemporaryPassword: Boolean = false,

    val disabledDate: LocalDate,
    ) : BaseEntity() {
}