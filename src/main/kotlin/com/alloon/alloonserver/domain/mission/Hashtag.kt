package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*

@Entity
class Hashtag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    val id: Long? = null,

    @Column(nullable = false, length = 31)
    val hashtag: String,
) : BasePermanentEntity() {
}