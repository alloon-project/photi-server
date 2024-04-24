package com.alloon.alloonserver.domain.mission

import org.springframework.data.jpa.repository.JpaRepository

interface MissionMemberRepository : JpaRepository<MissionMember, Long> {
}