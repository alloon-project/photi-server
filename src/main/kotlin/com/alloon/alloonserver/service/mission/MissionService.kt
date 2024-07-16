package com.alloon.alloonserver.service.mission

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.mission.dto.MissionServiceCreateMissionDto
import com.alloon.alloonserver.service.mission.response.MissionCreateResponse
import com.alloon.alloonserver.service.s3.S3Service
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.*

@Service
@Validated
@Transactional(readOnly = true)
class MissionService(
    private val missionRepository: MissionRepository,
    private val missionMemberRepository: MissionMemberRepository,
    private val missionHashtagRepository: MissionHashtagRepository,
    private val missionRuleRepository: MissionRuleRepository,
    private val missionTemplateImageRepository: MissionTemplateImageRepository,
    private val hashtagRepository: HashtagRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) {

    @Transactional
    fun createMission(userId: Long, @Valid request: MissionServiceCreateMissionDto): MissionCreateResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val missionMember = request.toMissionMember(user)
        missionRepository.save(missionMember.mission)
        missionMemberRepository.save(missionMember)

        val missionRules = request.toMissionRule(missionMember.mission)
        missionRules.isNotEmpty().let { missionRuleRepository.saveAll(missionRules) }

//        val hashtags = request.hashtags.map { it.hashtag }.toList()
//        hashtagRepository.findAll(hashtags)
        val missionHashtags = request.toMissionHashtag(missionMember.mission)

        hashtagRepository.saveAll(missionHashtags.map { it.hashtag })
        missionHashtagRepository.saveAll(missionHashtags)

        return MissionCreateResponse(missionMember, missionRules, missionHashtags.map { it.hashtag })
    }

    fun getAllMissionTemplateImages(now: LocalDateTime): List<String> {
        return missionTemplateImageRepository.findAllImageUrl(now)
    }

    @Transactional
    fun uploadMissionImage(userId: Long, file: MultipartFile?): String {
        return s3Service.uploadFile(file, "users/$userId/missions", UUID.randomUUID().toString())
    }
}