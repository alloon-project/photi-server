package com.alloon.alloonserver.common.response

import org.springframework.data.domain.Slice

data class SliceResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
) {

    companion object {

        fun <T> of(sliceContent: Slice<T>): SliceResponse<T> {
            return SliceResponse(
                sliceContent.content,
                sliceContent.number,
                sliceContent.size,
                sliceContent.isFirst,
                sliceContent.isLast,
            )
        }
    }
}