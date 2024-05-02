package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BaseEntity
import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class MissionTemplateImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_template_image_id")
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

    @Column(nullable = false)
    val sort: Int,
) : BasePermanentEntity()