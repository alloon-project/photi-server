package com.alloon.alloonserver.domain.base

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BasePermanentEntity(
    @Column(nullable = false)
    var isDeleted: Boolean = false,

    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : BaseEntity(createdAt, updatedAt)