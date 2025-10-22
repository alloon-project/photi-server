package com.photi.core.domain.appversion.service.command

import com.photi.core.domain.appversion.dto.AppVersionDto
import com.photi.core.domain.appversion.model.AppVersion
import com.photi.core.domain.appversion.model.repository.AppVersionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AppVersionCommandService(
    private val appVersionRepository: AppVersionRepository,
) {

    fun createAppVersion(dto: AppVersionDto): AppVersion {
        val appVersion = AppVersion(os = dto.os, minVersion = dto.appVersion)
        return appVersionRepository.save(appVersion)
    }
}
