package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.base.BaseEntity
import jakarta.persistence.*

@Entity
class Contact(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 127)
    val email: String,
    @Column(nullable = false, length = 6)
    var verificationCode: String,

    @Column(nullable = false)
    var isVerified: Boolean = false,
    ) : BaseEntity() {

    fun changeVerificationCode(verificationCode: String) {
        this.verificationCode = verificationCode
        this.isVerified = false
    }
}