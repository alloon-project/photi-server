package com.photi.server.domain.inquiry

import com.photi.server.domain.base.BaseEntity
import com.photi.server.domain.user.User
import jakarta.persistence.*

@Entity
class Inquiry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    val type: InquiryCategoryType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User?,

    @Column(nullable = false, length = 120)
    val content: String
) : BaseEntity() {
}