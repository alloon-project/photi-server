package com.photi.core.domain.user.dto

import java.time.LocalDateTime

data class FindUserDeletedDateDto(
    val deletedDate: LocalDateTime?,
)
