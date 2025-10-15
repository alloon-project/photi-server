package com.photi.core.domain.appversion.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.utils.AppVersionUtil
import jakarta.persistence.*

@Entity
class AppVersion(
    os: OsType,
    minVersion: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_version_id")
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    var os: OsType = os
        protected set

    @Column(nullable = false, length = 10)
    var minVersion: String = minVersion
        protected set

    fun compareVersions(version: String) =
        AppVersionUtil.compareVersions(version, minVersion) < ZERO

    fun changeMinVersion(minVersion: String) {
        this.minVersion = minVersion
    }

    companion object {
        private const val ZERO = 0
    }
}
