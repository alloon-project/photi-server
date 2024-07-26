package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.mission.custom.MissionCustomRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface MissionRepository : JpaRepository<Mission, Long>, MissionCustomRepository {

    fun findPopularByServiceStatus(status: ServiceStatus, pageable: Pageable): List<Mission>
}