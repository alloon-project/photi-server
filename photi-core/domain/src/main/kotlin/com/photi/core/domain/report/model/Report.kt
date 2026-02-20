package com.photi.core.domain.report.model

import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*

@Entity
class Report(
    reporterId: Long,
    targetId: Long,
    category: CategoryType,
    reason: ReasonType,
    content: String? = null,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", nullable = false)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var reporterId: Long = reporterId
        protected set

    @Column(nullable = false)
    var targetId: Long = targetId
        protected set

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 20)
    var category: CategoryType = category
        protected set

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 15)
    var reason: ReasonType = reason
        protected set

    @Column(nullable = true, length = 120)
    var content: String? = content
        protected set
}
