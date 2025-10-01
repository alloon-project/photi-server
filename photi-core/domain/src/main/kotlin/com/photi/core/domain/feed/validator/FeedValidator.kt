package com.photi.core.domain.feed.validator

import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.core.domain.feed.query.FeedQueryService
import org.springframework.stereotype.Component

@Component
class FeedValidator(
    private val feedQueryService: FeedQueryService,
) {

    fun validateExistsBy(targetId: Long) {
        if (!feedQueryService.existsBy(targetId)) {
            throw CustomException(ExceptionCode.FEED_NOT_FOUND)
        }
    }
}
