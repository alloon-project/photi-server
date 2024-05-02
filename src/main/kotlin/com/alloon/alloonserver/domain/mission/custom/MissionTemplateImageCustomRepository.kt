package com.alloon.alloonserver.domain.mission.custom

import com.alloon.alloonserver.domain.mission.MissionTemplateImage
import java.time.LocalDateTime

interface MissionTemplateImageCustomRepository {

    fun findAllImageUrl(now: LocalDateTime): MutableList<String>
}