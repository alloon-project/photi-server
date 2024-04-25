package com.alloon.alloonserver.domain.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @CreatedDate
    @Column(nullable = true)
    var createDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column(nullable = true)
    var updateDateTime: LocalDateTime? = null,
)