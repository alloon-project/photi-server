package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.mission.custom.HashtagCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface HashtagRepository : JpaRepository<Hashtag, Long>, HashtagCustomRepository {
}