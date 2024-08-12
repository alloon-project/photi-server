package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.MissionMember
import com.alloon.alloonserver.service.mission.dto.FindMissionMembersDto

interface MissionMemberCustomRepository {

    fun find(id: Long): MissionMember?

    fun findByUserIdAndMissionId(userId: Long, missionId: Long): MissionMember?

    fun findAllByMissionId(userId: Long, missionId: Long): List<FindMissionMembersDto>
}