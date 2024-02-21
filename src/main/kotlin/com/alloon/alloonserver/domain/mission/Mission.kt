package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Mission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    val id: BigInteger? = null,

    @Column(nullable = false, length = 31)
    var missionName: String,
    @Column(nullable = false, length = 511)
    val description: String,
    val rule: String? = null,
    val goal: String? = null,
    val imageUrl: String? = null,

    @Column(nullable = false)
    val visitedCnt: Long = 1,
    @Column(nullable = false)
    val currentMemberCnt: Long = 0,

    @Column(nullable = false)
    val isRecruiting: Boolean = true,

    @Column(nullable = false)
    val startedDate: LocalDate,
    @Column(nullable = false)
    val endedDate: LocalDate,
) : BasePermanentEntity() {
}