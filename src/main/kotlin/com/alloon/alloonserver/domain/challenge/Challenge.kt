package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.domain.base.BasePermanentEntity
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
class Challenge(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
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
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rules: MutableList<ChallengeRule> = mutableListOf(),

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = ChallengeListStringConverter::class)
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

    fun addChallengeRule(rule: ChallengeRule) {
        rules.add(rule)
        rule.challenge = this
    }

    companion object {

        fun toEntity(dto: CreateChallengeDto): Challenge {
            val challenge = Challenge(
                name = dto.name,
                isPublic = dto.isPublic,
                goal = dto.goal,
                proveTime = dto.proveTime,
                endDate = dto.endDate,
                imageUrl = dto.imageUrl,
                hashtags = dto.hashtags.map { it.hashtag },
            )
            dto.rules.forEach {
                challenge.addChallengeRule(ChallengeRule(rule = it.rule))
            }
            return challenge
        }
    }
}