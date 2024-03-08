package com.alloon.alloonserver.domain.report

import com.alloon.alloonserver.domain.base.BaseEntity
import com.alloon.alloonserver.domain.mission.Mission
import com.alloon.alloonserver.domain.user.User
import jakarta.persistence.*

@Entity
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    val reporter: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    val mission: Mission? = null,
) : BaseEntity() {
}