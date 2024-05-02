package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.mission.custom.MissionTemplateImageCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface MissionTemplateImageRepository :
    JpaRepository<MissionTemplateImage, Long>,
    MissionTemplateImageCustomRepository {
}