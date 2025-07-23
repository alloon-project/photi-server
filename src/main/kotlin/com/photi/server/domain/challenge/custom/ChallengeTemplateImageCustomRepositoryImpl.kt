package com.photi.server.domain.challenge.custom

import com.photi.server.domain.base.ServiceStatus
import com.photi.server.domain.challenge.QChallengeTemplateImage.challengeTemplateImage
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChallengeTemplateImageCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ChallengeTemplateImageCustomRepository {

    override fun findAllImageUrl(now: LocalDateTime): MutableList<String> {
        return queryFactory
            .select(challengeTemplateImage.imageUrl)
            .from(challengeTemplateImage)
            .where(
                challengeTemplateImage.startDateTime.loe(now),
                challengeTemplateImage.endDateTime.goe(now),
                challengeTemplateImage.serviceStatus.eq(ServiceStatus.ACTIVE)
            )
            .fetch()
    }
}