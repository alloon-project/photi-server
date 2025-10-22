package com.photi.core.domain.inquiry.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class Inquiry(
    userId: Long,
    category: CategoryType,
    content: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var userId: Long = userId
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    var category: CategoryType = category
        protected set

    @Column(nullable = false, length = 120)
    var content: String = content
        protected set
}
