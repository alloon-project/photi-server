package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.base.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false, length = 6)
    val verificationCode: String,
    @Column(nullable = false)
    var isVerified: Boolean = false,

    createdAt: LocalDateTime? = null,
    updatedAt: LocalDateTime? = null,
    ) : BaseEntity(createdAt, updatedAt) {
}