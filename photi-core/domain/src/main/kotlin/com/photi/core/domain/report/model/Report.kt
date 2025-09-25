package com.photi.core.domain.report.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class Report(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    val id: Long? = null,

    @Column(nullable = false)
    val reporterId: Long,

    @Column(nullable = false)
    val targetId: Long,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 20)
    val category: ReportCategoryType,

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 15)
    val reason: ReportReasonType,

    @Column(length = 120)
    val content: String? = null,
) : BaseEntity()
