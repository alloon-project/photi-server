package com.photi.core.domain.appversion.service.query

import com.photi.core.domain.appversion.model.OsType
import com.photi.core.domain.appversion.model.repository.AppVersionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AppVersionQueryService(
    private val appVersionRepository: AppVersionRepository,
) {

    fun getOsPolicyBy(os: OsType) = appVersionRepository.findByOs(os)
}
