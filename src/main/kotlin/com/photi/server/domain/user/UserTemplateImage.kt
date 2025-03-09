package com.photi.server.domain.user

import com.photi.server.domain.base.BasePermanentEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class UserTemplateImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_template_image_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    val admin: User?,

    @Column(nullable = false, length = 500)
    val imageUrl: String,

    @Column(nullable = false)
    val startDateTime: LocalDateTime,
    @Column(nullable = false)
    val endDateTime: LocalDateTime,
) : BasePermanentEntity() {
}