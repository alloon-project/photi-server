package com.photi.core.domain.feed.service.command

import com.photi.core.domain.feed.dto.RegisterFeedDto
import com.photi.core.domain.feed.dto.RegisterFeedRequestDto
import com.photi.core.domain.feed.model.repository.FeedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FeedCommandService(
    private val feedRepository: FeedRepository,
) {

    fun createFeed(
        dto: RegisterFeedRequestDto,
        userId: Long,
        challengeMemberId: Long,
        challengeId: Long,
    ): RegisterFeedDto {
        val feed = feedRepository.save(dto.toEntity(userId, challengeMemberId, challengeId))
        return RegisterFeedDto.of(feed)
    }

    fun deleteFeed(feedId: Long) {
        feedRepository.deleteById(feedId)
    }
}
