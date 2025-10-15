package com.photi.core.domain.appversion.service

import com.photi.core.domain.appversion.dto.AppVersionDto
import com.photi.core.domain.appversion.exception.AppVersionException
import com.photi.core.domain.appversion.service.command.AppVersionCommandService
import com.photi.core.domain.appversion.service.query.AppVersionQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AppVersionService(
    private val appVersionQueryService: AppVersionQueryService,
    private val appVersionCommandService: AppVersionCommandService,
) {

    fun checkMinVersion(dto: AppVersionDto): Boolean {
        val osPolicy = appVersionQueryService.getOsPolicyBy(dto.os)
            ?: throw AppVersionException.NotFoundOsPolicyException()
        return osPolicy.compareVersions(dto.appVersion)
    }

    @Transactional
    fun updateMinVersion(dto: AppVersionDto) {
        val osPolicy = appVersionQueryService.getOsPolicyBy(dto.os)
            ?: appVersionCommandService.createAppVersion(dto)
        osPolicy.changeMinVersion(dto.appVersion)
    }
}
