package com.photi.core.domain.appversion.model

import com.photi.core.domain.common.model.BaseEntity
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

    fun updateMinVersion(minVersion: String) {
        this.minVersion = minVersion
    }
}
