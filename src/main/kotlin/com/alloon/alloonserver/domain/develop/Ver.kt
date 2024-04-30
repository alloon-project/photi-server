package com.alloon.alloonserver.domain.develop

import com.alloon.alloonserver.domain.base.BaseEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class Ver(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ver_id")
    val id: Int? = null,

    @Column(nullable = false, length = 10)
    val version: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    val admin: User?,
) : BaseEntity()