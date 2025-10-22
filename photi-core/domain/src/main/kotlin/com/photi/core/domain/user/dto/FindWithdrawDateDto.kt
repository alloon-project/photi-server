package com.photi.core.domain.user.dto

import com.photi.core.domain.user.model.User
import java.time.LocalDateTime

data class FindWithdrawDateRequestDto(
    val email: String,
)

data class FindWithdrawDateDto(
    val withdrawDate: LocalDateTime?,
) {

    companion object {

        fun of(user: User) = FindWithdrawDateDto(user.deletedDate)
    }
}
