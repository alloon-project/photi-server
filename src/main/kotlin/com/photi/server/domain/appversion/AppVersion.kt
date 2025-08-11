package com.photi.server.domain.appversion

import com.photi.server.domain.base.BaseEntity
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
    val minVersion: String,
) : BaseEntity()
