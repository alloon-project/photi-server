package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeExampleImages(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_example_images_id")
    val id: Long? = null,

    @Column(nullable = false, length = 500)
    val imageUrl: String,
) : BaseEntity()
