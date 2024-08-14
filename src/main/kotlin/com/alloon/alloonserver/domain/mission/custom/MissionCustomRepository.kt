package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.service.mission.dto.FindPopularMissionsDto

interface MissionCustomRepository {

    fun find(id: Long): Mission?

    fun findPopular(): List<FindPopularMissionsDto>

    fun findInfoById(id: Long): Mission?
}