package com.alloon.alloonserver.domain.develop

import com.alloon.alloonserver.domain.develop.custom.VerCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface VerRepository : JpaRepository<Ver, Int>, VerCustomRepository {
}