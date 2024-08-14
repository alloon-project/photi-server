package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.base.ServiceStatus.ACTIVE
import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.domain.mission.QMissionMember.missionMember
import com.alloon.alloonserver.service.mission.dto.FindMissionMembersDto
import com.alloon.alloonserver.service.mission.dto.QFindMissionMembersDto
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class MissionMemberCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : MissionMemberCustomRepository {

    override fun find(id: Long): MissionMember? {
        return queryFactory
            .selectFrom(missionMember)
            .where(
                missionMember.id.eq(id),
                eqServiceStatus(ACTIVE)
            ).fetchFirst()
    }

    override fun findByUserIdAndMissionId(userId: Long, missionId: Long): MissionMember? {
        return queryFactory
            .selectFrom(missionMember)
            .join(missionMember.user).fetchJoin()
            .join(missionMember.mission).fetchJoin()
            .where(
                missionMember.user.id.eq(userId),
                missionMember.mission.id.eq(missionId),
                eqServiceStatus(ACTIVE)
            )
            .fetchFirst()
    }

    override fun findAllByMissionId(userId: Long, missionId: Long): List<FindMissionMembersDto> {
        val spec = CaseBuilder()
            .`when`(missionMember.isCreator.isTrue).then(1)
            .`when`(missionMember.user.id.eq(userId)).then(2)
            .otherwise(3)

        return queryFactory
            .select(
                QFindMissionMembersDto(
                    missionMember.id,
                    missionMember.user.username,
                    missionMember.user.imageUrl,
                    missionMember.isCreator,
                    missionMember.createDateTime,
                    missionMember.goal
                )
            )
            .from(missionMember)
            .where(missionMember.mission.id.eq(missionId))
            .orderBy(
                spec.asc(),
                missionMember.createDateTime.asc()
            )
            .fetch()
    }

    private fun eqServiceStatus(serviceStatus: ServiceStatus?): BooleanExpression? =
        serviceStatus?.let { missionMember.serviceStatus.eq(serviceStatus) }
}