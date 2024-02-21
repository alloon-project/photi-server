package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.base.BaseEntity
import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
class Contact(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    val id: BigInteger? = null,

    @Column(nullable = false, unique = true, length = 127)
    val email: String,
    @Column(nullable = false, length = 6)
    val verificationCode: String,

    @Column(nullable = false)
    val isVerified: Boolean = false,
    ) : BaseEntity() {
}