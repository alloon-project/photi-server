package com.alloon.alloonserver.domain.base

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BasePermanentEntity(

    @Column(nullable = false, length = 10)
    var serviceStatus: ServiceStatus = ServiceStatus.ACTIVE,

    createDateTime: LocalDateTime? = null,
    updateDateTime: LocalDateTime? = null,
    ) : BaseEntity(createDateTime, updateDateTime)