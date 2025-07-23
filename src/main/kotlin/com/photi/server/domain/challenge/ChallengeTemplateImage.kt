package com.photi.server.domain.challenge

import com.photi.server.domain.base.BasePermanentEntity
import com.photi.server.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class ChallengeTemplateImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_template_image_id")
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
) : BasePermanentEntity()