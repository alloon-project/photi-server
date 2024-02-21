package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.math.BigInteger
import java.time.LocalDateTime

@Entity
class Hashtag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    val id: BigInteger? = null,

    @Column(nullable = false, length = 31)
    val hashtag: String,
) : BasePermanentEntity() {
}