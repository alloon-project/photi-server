package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.mission.MissionTemplateImage
import com.alloon.alloonserver.domain.mission.QMissionTemplateImage.missionTemplateImage
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MissionTemplateImageCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : MissionTemplateImageCustomRepository {

    override fun findAllImageUrl(now: LocalDateTime): MutableList<String> {
        return queryFactory
            .select(missionTemplateImage.imageUrl)
            .from(missionTemplateImage)
            .where(
                missionTemplateImage.startDateTime.loe(now),
                missionTemplateImage.endDateTime.goe(now),
                missionTemplateImage.serviceStatus.eq(ServiceStatus.ACTIVE)
            )
            .orderBy(missionTemplateImage.sort.asc())
            .fetch()
    }
}