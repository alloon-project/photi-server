package com.alloon.alloonserver.domain.user

import com.alloon.alloonserver.domain.base.BaseEntity
import jakarta.persistence.*

@Table(name = "users")
@Entity
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    val contact: Contact,

    @Column(nullable = false, length = 20)
    val username: String,
    @Column(nullable = false)
    var password: String,
    val imageUrl: String? = null,

    @Column(nullable = false)
    var isTemporaryPassword: Boolean = false,
    ) : BaseEntity() {

    fun resetPassword(password: String) {
        this.password = password
        this.isTemporaryPassword = true
    }

    fun changePassword(password: String) {
        this.password = password
        this.isTemporaryPassword = false
    }
}
