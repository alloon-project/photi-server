package com.photi.server.domain.appversion

import org.springframework.data.jpa.repository.JpaRepository

interface AppVersionRepository : JpaRepository<AppVersion, Long> {

    fun findByOs(os: OsType): AppVersion?
}
