package com.photi.server.service.appversion

import com.photi.server.common.constant.ExceptionCode
import com.photi.server.common.response.CustomException
import com.photi.server.common.util.AppVersionUtility
import com.photi.server.domain.appversion.AppVersion
import com.photi.server.domain.appversion.AppVersionRepository
import com.photi.server.service.appversion.dto.AppVersionDto
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
        val forceUpdate = AppVersionUtility.compareVersions(dto.appVersion, osPolicy.minVersion)
        return forceUpdate < 0
    }

    @Transactional
    fun updateAppVersion(dto: AppVersionDto) {
        val osPolicy = appVersionRepository.findByOs(dto.os)
            ?: appVersionRepository.save(AppVersion(os = dto.os, minVersion = dto.appVersion))
        osPolicy.updateMinVersion(dto.appVersion)
    }
}
