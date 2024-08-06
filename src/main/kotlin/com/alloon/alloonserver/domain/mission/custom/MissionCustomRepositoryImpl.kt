package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.QMission.mission
import com.alloon.alloonserver.domain.mission.custom.dto.PopularMissionDto
import com.alloon.alloonserver.domain.mission.custom.dto.QPopularMissionDto
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class MissionCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : MissionCustomRepository {

    override fun find(id: Long): Mission? {
        return queryFactory
            .selectFrom(mission)
            .where(
                mission.id.eq(id),
                eqServiceStatus(ACTIVE)
            ).fetchFirst()
    }

    override fun findPopular(): List<PopularMissionDto> {
        return queryFactory
            .select(QPopularMissionDto(mission.id, mission.name, mission.endDate, mission.imageUrl, mission.hashtags))
            .from(mission)
            .where(eqServiceStatus(ACTIVE))
            .orderBy(mission.visitCnt.desc())
            .limit(5)
            .fetch()
    }

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { mission.serviceStatus.eq(serviceStatus) }
}