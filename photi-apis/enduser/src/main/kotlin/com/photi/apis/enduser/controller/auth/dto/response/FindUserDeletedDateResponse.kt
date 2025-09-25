package com.photi.apis.enduser.controller.auth.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.photi.core.domain.user.dto.FindUserDeletedDateDto
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "회원 탈퇴 날짜 조회 응답 객체")
data class FindUserDeletedDateResponse(

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "회원 탈퇴 날짜", example = "2025-07-15")
    val deletedDate: LocalDate?,
) {

    companion object {

        fun of(dto: FindUserDeletedDateDto) =
            FindUserDeletedDateResponse(dto.deletedDate?.toLocalDate())
    }
}
