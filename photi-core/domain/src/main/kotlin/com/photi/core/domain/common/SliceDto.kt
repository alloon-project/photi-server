package com.photi.core.domain.common

import org.springframework.data.domain.Slice

data class SliceDto<T>(
    val content: List<T>,
    val number: Int,
    val size: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
)

fun <T> Slice<T>.toSliceDto(): SliceDto<T> {
    return SliceDto(this.content, this.number, this.size, this.isFirst, this.isLast)
}
