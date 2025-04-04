package com.photi.server.domain.report

import com.photi.server.domain.base.BasePermanentEntity
import com.photi.server.domain.user.User
import jakarta.persistence.*

@Entity
class ReportCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_category_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    val admin: User?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    val type: ReportCategoryType,

    @Column(nullable = false, length = 30)
    val description: String,

    @Column(nullable = false)
    val sort: Int,
) : BasePermanentEntity() {
}