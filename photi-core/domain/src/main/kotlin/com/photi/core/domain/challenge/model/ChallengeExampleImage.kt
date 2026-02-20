package com.photi.core.domain.challenge.model

import jakarta.persistence.*

@Entity
class ChallengeExampleImage(
    imageUrl: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_example_image_id")
    var id: Long? = null
        protected set

    @Column(nullable = false, length = 500)
    var imageUrl: String = imageUrl
        protected set
}
