package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.base.BaseEntity
import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class Inquiry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_category_id", nullable = false)
    val inquiryCategory: InquiryCategory,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User?,

    @Column(nullable = false, length = 100)
    val content: String
) : BaseEntity() {
}