package com.photi.core.domain.feed.query

import com.photi.core.domain.common.model.ServiceStatus
import com.photi.core.domain.feed.model.repository.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedQueryService(
    private val feedRepository: FeedRepository,
) {

    fun existsBy(id: Long) =
        feedRepository.existsByIdAndServiceStatus(id, ServiceStatus.ACTIVE)
}
