package com.photi.server.domain.inquiry

import com.photi.server.domain.base.BasePermanentEntity
import com.photi.server.domain.user.User
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