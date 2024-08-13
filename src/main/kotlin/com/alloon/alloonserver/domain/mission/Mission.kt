package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.service.mission.dto.CreateMissionDto
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

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
    val name: String,

    @Column(nullable = false)
    val isPublic: Boolean,

    @Column(nullable = false, length = 120)
    val goal: String,

    @Column(nullable = false)
    val proveTime: LocalTime,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Column(nullable = false, length = 500)
    val imageUrl: String,

    @Column(nullable = false)
    @OneToMany(mappedBy = "mission", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rules: MutableList<MissionRule> = mutableListOf(),

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = MissionListStringConverter::class)
    val hashtags: List<String> = listOf(),

    @Column(nullable = false)
    val startDate: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val currentMemberCnt: Int = 1,

    //TODO redis 사용하면 hyperlog 로 변경 필요함.
    @Column(nullable = false)
    val visitCnt: Int = 0,

    @Column(nullable = false)
    val isRecruit: Boolean = true,
) : BasePermanentEntity() {

    fun addMissionRule(rule: MissionRule) {
        rules.add(rule)
        rule.mission = this
    }

    companion object {

        fun toEntity(dto: CreateMissionDto): Mission {
            val mission = Mission(
                name = dto.name,
                isPublic = dto.isPublic,
                goal = dto.goal,
                proveTime = dto.proveTime,
                endDate = dto.endDate,
                imageUrl = dto.imageUrl,
                hashtags = dto.hashtags.map { it.hashtag },
            )
            dto.rules.forEach {
                mission.addMissionRule(MissionRule(rule = it.rule))
            }
            return mission
        }
    }
}