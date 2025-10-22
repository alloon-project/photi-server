package com.photi.core.domain.feed.service.command

import com.photi.core.domain.feed.dto.RegisterFeedDto
import com.photi.core.domain.feed.model.repository.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedCommandService(
    private val feedRepository: FeedRepository,
) {

    fun createFeed(dto: RegisterFeedDto, userId: Long, challengeMemberId: Long, challengeId: Long) =
        feedRepository.save(dto.toEntity(userId, challengeMemberId, challengeId)).id!!

    fun deleteFeed(feedId: Long) {
        feedRepository.deleteById(feedId)
    }
}
