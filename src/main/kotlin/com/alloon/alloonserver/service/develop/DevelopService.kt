package com.alloon.alloonserver.service.develop

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

    fun needForceUpdate(version: String): com.alloon.alloonserver.service.develop.response.DevelopIsNeedForceUpdateResponse {
        return com.alloon.alloonserver.service.develop.response.DevelopIsNeedForceUpdateResponse(
            verRepository.exists(
                version
            )
        )
    }
}