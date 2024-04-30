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

    @Column(nullable = false, length = 20, unique = true)
    val username: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false, length = 500)
    var imageUrl: String,

    @Column(nullable = false)
    var temporaryPasswordYn: Boolean = false,
    ) : BaseEntity() {

    fun resetPassword(password: String) {
        this.password = password
        this.temporaryPasswordYn = true
    }

    fun changePassword(password: String) {
        this.password = password
        this.temporaryPasswordYn = false
    }

    fun changeImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }
}
