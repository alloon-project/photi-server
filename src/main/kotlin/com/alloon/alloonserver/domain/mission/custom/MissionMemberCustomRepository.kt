package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.MissionMember

interface MissionMemberCustomRepository {

    fun find(id: Long): MissionMember?
}