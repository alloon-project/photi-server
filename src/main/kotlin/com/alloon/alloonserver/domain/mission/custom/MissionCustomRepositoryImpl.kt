package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.QMission.mission
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

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { mission.serviceStatus.eq(serviceStatus) }
}