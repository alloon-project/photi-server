package com.photi.core.domain.common.model

import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BasePermanentEntity(

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    var serviceStatus: ServiceStatus = ServiceStatus.ACTIVE,

    createdDateTime: LocalDateTime? = null,
    lastModifiedDateTime: LocalDateTime? = null,
) : BaseEntity(createdDateTime, lastModifiedDateTime)
