package com.alloon.alloonserver.api.service.develop

import com.alloon.alloonserver.api.service.develop.response.DevelopIsNeedForceUpdateResponse
import com.alloon.alloonserver.domain.develop.VerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DevelopService(
    @Value("\${api.name}") private val serverName: String,
    private val verRepository: VerRepository,
) {

    fun getServerName(): String {
        return serverName
    }

    fun needForceUpdate(version: String): DevelopIsNeedForceUpdateResponse {
        return DevelopIsNeedForceUpdateResponse(verRepository.exists(version))
    }
}