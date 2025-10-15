package com.photi.core.domain.appversion.model

import com.photi.core.domain.common.model.BaseEntity
import com.photi.utils.AppVersionUtil
import jakarta.persistence.*

@Entity
class AppVersion(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_version_id")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    val os: OsType,

    @Column(nullable = false, length = 10)
    var minVersion: String,
) : BaseEntity() {

    fun compareVersions(version: String) =
        AppVersionUtil.compareVersions(version, minVersion) < ZERO

    fun changeMinVersion(minVersion: String) {
        this.minVersion = minVersion
    }

    companion object {
        private const val ZERO = 0
    }
}
