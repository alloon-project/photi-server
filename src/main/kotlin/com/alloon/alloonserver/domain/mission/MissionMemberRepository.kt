package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.mission.custom.MissionMemberCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface MissionMemberRepository : JpaRepository<MissionMember, Long>, MissionMemberCustomRepository {
}