package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.Mission

interface MissionCustomRepository {

    fun find(id: Long): Mission?
}