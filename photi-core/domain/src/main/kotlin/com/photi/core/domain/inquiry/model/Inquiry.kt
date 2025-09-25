package com.photi.core.domain.inquiry.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.core.domain.user.model.User
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
) : BaseEntity()
