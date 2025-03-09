package com.photi.server.domain.develop

import com.photi.server.domain.develop.custom.VerCustomRepository
import org.springframework.data.jpa.repository.JpaRepository

interface VerRepository : JpaRepository<Ver, Int>, VerCustomRepository {
}