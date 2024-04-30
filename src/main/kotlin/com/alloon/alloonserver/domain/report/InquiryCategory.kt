package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class InquiryCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_category_id")
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    val admin: User?,

    @Column(nullable = false, length = 30)
    val description: String,

    @Column(nullable = false)
    val sort: Int,
) : BasePermanentEntity() {
}