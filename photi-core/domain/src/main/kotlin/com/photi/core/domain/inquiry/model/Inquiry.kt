package com.photi.core.domain.inquiry.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class Inquiry(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    val category: CategoryType,

    @Column(nullable = false, length = 120)
    val content: String,
) : BaseEntity()
