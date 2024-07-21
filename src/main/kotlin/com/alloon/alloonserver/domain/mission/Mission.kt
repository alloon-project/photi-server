package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import jakarta.persistence.*
import java.time.LocalDate

/**
 * Mission Entity
 * missionName : 미션 이름
 * description : 미션 설명
 * goal : 미션 목표
 * imageUrl : 이미지 url
 *
 */
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

    @Column(nullable = false, length = 30)
    val goal: String,

    @Column(nullable = false, length = 500)
    val imageUrl: String,

    //TODO redis 사용하면 hyperlog 로 변경 필요함.
    @Column(nullable = false)
    val visitCnt: Int = 0,

    @Column(nullable = false)
    val currentMemberCnt: Int = 1,

    @Column(nullable = false)
    val recruitYn: Boolean = true,

    @Column(nullable = false)
    val startDate: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val endDate: LocalDate,


    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = MissionListStringConverter::class)
    val hashtags : List<String> = listOf()
) : BasePermanentEntity() {
}