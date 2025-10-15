package com.photi.core.domain.challenge.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class ChallengeExampleImage(
    imageUrl: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_example_image_id")
    var id: Long? = null
        protected set

    @Column(nullable = false, length = 500)
    var imageUrl: String = imageUrl
        protected set
}
