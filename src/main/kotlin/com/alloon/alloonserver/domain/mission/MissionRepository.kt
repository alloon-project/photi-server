package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.mission.custom.MissionCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface MissionRepository : JpaRepository<Mission, Long>, MissionCustomRepository {
}