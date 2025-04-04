package com.photi.server.domain.develop

import com.photi.server.domain.base.BaseEntity
import com.photi.server.domain.user.User
import jakarta.persistence.*

@Entity
class Ver(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ver_id")
    val id: Long? = null,

    @Column(nullable = false, length = 10)
    val version: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    val admin: User?,
) : BaseEntity()