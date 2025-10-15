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
import com.photi.core.domain.common.model.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
class Challenge(
    name: String,
    isPublic: Boolean,
    goal: String,
    proveTime: LocalTime,
    endDate: LocalDate,
    imageUrl: String,
    invitationCode: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    var id: Long? = null
        protected set

    @Column(nullable = false, length = 16)
    var name: String = name
        protected set

    @Column(nullable = false)
    var isPublic: Boolean = isPublic
        protected set

    @Column(nullable = false, length = 120)
    var goal: String = goal
        protected set

    @Column(nullable = false)
    var proveTime: LocalTime = proveTime
        protected set

    @Column(nullable = false)
    var endDate: LocalDate = endDate
        protected set

    @Column(nullable = false, length = 500)
    var imageUrl: String = imageUrl
        protected set

    @Column(nullable = false, length = 5)
    var invitationCode: String = invitationCode
        protected set

    @Column(nullable = false)
    var startDate: LocalDate = LocalDate.now()
        protected set

    @Column(nullable = false)
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    var rules: MutableList<ChallengeRule> = mutableListOf()
        protected set

    @Column(nullable = false)
    @OneToMany(mappedBy = "challenge", cascade = [CascadeType.ALL], orphanRemoval = true)
    var hashtags: MutableList<ChallengeHashtag> = mutableListOf()
        protected set

    @Column(nullable = false, length = 15)
    @Enumerated(value = EnumType.STRING)
    var status: StatusType = StatusType.ACTIVE
        protected set

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
        status = StatusType.END
    }

    private fun changeImageUrl(s3Port: ChallengeS3Port, imageUrl: String) {
        deleteImage(s3Port)
        this.imageUrl = imageUrl
    }

    private fun deleteImage(s3Port: ChallengeS3Port) {
        s3Port.deleteImage(imageUrl, DirectoryType.CHALLENGES)
    }
}
