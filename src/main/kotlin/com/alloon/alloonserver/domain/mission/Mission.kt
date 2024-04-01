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

    @Column(nullable = false, length = 30)
    var missionName: String,
    @Column(nullable = false, length = 500)
    val description: String,
    @Column(length = 500)
    val rule: String? = null,
    @Column(length = 500)
    val goal: String? = null,
    val imageUrl: String? = null,

    @Column(nullable = false)
    val visitedCnt: Int = 1,
    @Column(nullable = false)
    val currentMemberCnt: Int = 0,

    @Column(nullable = false)
    val isRecruiting: Boolean = true,

    @Column(nullable = false)
    val startedDate: LocalDate,
    @Column(nullable = false)
    val endedDate: LocalDate,
) : BasePermanentEntity() {
}