package com.photi.server.service.user.dto

import java.time.LocalDateTime

data class FindUserDeletedDateDto(
    val deletedDate: LocalDateTime?,
)
