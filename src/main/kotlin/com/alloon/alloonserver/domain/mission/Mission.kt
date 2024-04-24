package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Mission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    val id: Long? = null,

    @Column(nullable = false, length = 16)
    var missionName: String,
    @Column(nullable = false, length = 120)
    val description: String,
    @Column(length = 30)
    val goal: String? = null,
    @Column(length = 30)
    val rule: String? = null,
    @Column(nullable = false, length = 500)
    val imageUrl: String,

    @Column(nullable = false)
    val visitCnt: Int = 0,
    @Column(nullable = false)
    val currentMemberCnt: Int = 1,

    @Column(nullable = false)
    val isRecruiting: Boolean = true,

    @Column(nullable = false)
    val startDate: LocalDate = LocalDate.now(),
    @Column(nullable = false)
    val endDate: LocalDate,
) : BasePermanentEntity() {
}