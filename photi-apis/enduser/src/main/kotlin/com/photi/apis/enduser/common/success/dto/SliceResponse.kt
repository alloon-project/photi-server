package com.photi.apis.enduser.common.success.dto

import com.photi.core.domain.common.SliceDto

data class SliceResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
) {

    companion object {

        fun <T, R> of(dto: SliceDto<T>, mapper: (T) -> R): SliceResponse<R> {
            return SliceResponse(
                dto.content.map(mapper),
                dto.number,
                dto.size,
                dto.isFirst,
                dto.isLast,
            )
        }
    }
}
