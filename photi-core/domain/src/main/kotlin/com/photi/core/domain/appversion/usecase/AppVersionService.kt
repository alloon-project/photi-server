package com.photi.core.domain.appversion.usecase

import com.photi.core.domain.appversion.dto.AppVersionDto
import com.photi.core.domain.appversion.model.AppVersion
import com.photi.core.domain.appversion.model.repository.AppVersionRepository
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import com.photi.utils.AppVersionUtil.compareVersions
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AppVersionService(
    private val appVersionRepository: AppVersionRepository,
) {

    fun checkAppVersion(dto: AppVersionDto): Boolean {
        val osPolicy = appVersionRepository.findByOs(dto.os)
            ?: throw CustomException(ExceptionCode.OS_POLICY_NOT_FOUND)
        val forceUpdate = compareVersions(dto.appVersion, osPolicy.minVersion)
        return forceUpdate < 0
    }

    @Transactional
    fun updateAppVersion(dto: AppVersionDto) {
        val osPolicy = appVersionRepository.findByOs(dto.os)
            ?: appVersionRepository.save(AppVersion(os = dto.os, minVersion = dto.appVersion))
        osPolicy.updateMinVersion(dto.appVersion)
    }
}
