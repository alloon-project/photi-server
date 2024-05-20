package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Entity
class Hashtag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false, length = 30)
    val tag: String,
) : BasePermanentEntity() {
}