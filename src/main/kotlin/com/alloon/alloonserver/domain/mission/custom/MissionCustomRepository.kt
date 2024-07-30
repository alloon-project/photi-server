package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.mission.custom.dto.PopularMissionDto

interface MissionCustomRepository {

    fun find(id: Long): Mission?

    fun findPopular(): List<PopularMissionDto>
}