package com.photi.core.domain.challenge.model

import com.photi.core.domain.challenge.dto.ChallengeHashtagDto
import com.photi.core.domain.challenge.dto.ChallengeRuleDto
import com.photi.core.domain.challenge.dto.UpdateChallengeDto
import com.photi.core.domain.challenge.port.ChallengeChallengeHistoryPort
import com.photi.core.domain.challenge.port.ChallengeChallengeMemberPort
import com.photi.core.domain.challenge.port.ChallengeS3Port
import com.photi.core.domain.challenge.service.HashtagService
import com.photi.core.domain.challenge.validator.ChallengeValidator
import com.photi.core.domain.challengemember.dto.RegisterChallengePersonalGoalDto
import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.common.model.BasePermanentEntity
import com.photi.core.domain.common.model.ServiceStatus
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
    var name: String,

    @Column(nullable = false)
    val isPublic: Boolean,

    @Column(nullable = false, length = 120)
    var goal: String,

    @Column(nullable = false)
    var proveTime: LocalTime,

    @Column(nullable = false)
    var endDate: LocalDate,

    @Column(nullable = false, length = 500)
    var imageUrl: String,

    @Column(length = 5)
    val invitationCode: String,

    @Column(nullable = false)
    val startDate: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    val rules: MutableList<ChallengeRule> = mutableListOf(),

    @Column(nullable = false)
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    val hashtags: MutableList<ChallengeHashtag> = mutableListOf(),
) : BasePermanentEntity() {

    fun createCreator(
        userId: Long,
        challengeMemberPort: ChallengeChallengeMemberPort,
        challengeHistoryPort: ChallengeChallengeHistoryPort,
    ) {
        // todo 동시성 제어 aop
        challengeMemberPort.createCreator(userId, id!!)
        challengeHistoryPort.increaseChallengeMember(id!!)
    }

    fun createMember(
        userId: Long,
        dto: RegisterChallengePersonalGoalDto,
        challengeMemberPort: ChallengeChallengeMemberPort,
        challengeHistoryPort: ChallengeChallengeHistoryPort,
    ) {
        // todo 동시성 제어 aop
        challengeMemberPort.createMember(userId, id!!, dto)
        challengeHistoryPort.increaseChallengeMember(id!!)
    }

    fun validateInvitationCode(challengeValidator: ChallengeValidator, invitationCode: String) =
        challengeValidator.validateMatches(this, invitationCode)

    fun change(dto: UpdateChallengeDto, s3Port: ChallengeS3Port, hashtagService: HashtagService) {
        hashtagService.changeHashtags(dto.hashtags)
        changeImageUrl(s3Port, dto.imageUrl)
        name = dto.name
        goal = dto.goal
        proveTime = dto.proveTime
        endDate = dto.endDate
        addRules(dto.rules)
        addHashtags(dto.hashtags)
    }

    fun addRules(newRules: List<ChallengeRuleDto>) {
        rules.clear()
        rules.addAll(newRules.map { it.toEntity(this) })
    }

    fun addHashtags(newHashtags: List<ChallengeHashtagDto>) {
        hashtags.clear()
        hashtags.addAll(newHashtags.map { it.toEntity(this) })
    }

    fun delete(s3Port: ChallengeS3Port, hashtagService: HashtagService) {
        hashtagService.deleteHashtags(hashtags)
        deleteImage(s3Port)
    }

    fun end() {
        serviceStatus = ServiceStatus.END
    }

    private fun changeImageUrl(s3Port: ChallengeS3Port, imageUrl: String) {
        deleteImage(s3Port)
        this.imageUrl = imageUrl
    }

    private fun deleteImage(s3Port: ChallengeS3Port) {
        s3Port.deleteImage(imageUrl, DirectoryType.CHALLENGES)
    }
}
