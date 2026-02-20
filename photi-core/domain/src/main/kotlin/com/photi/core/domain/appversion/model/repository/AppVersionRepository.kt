package com.photi.core.domain.appversion.model.repository

import com.photi.core.domain.appversion.model.AppVersion
import com.photi.core.domain.appversion.model.OsType
import org.springframework.data.jpa.repository.JpaRepository

interface AppVersionRepository : JpaRepository<AppVersion, Long> {

    fun findByOs(os: OsType): AppVersion?
}
