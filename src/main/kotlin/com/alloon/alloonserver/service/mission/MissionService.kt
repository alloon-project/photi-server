package com.alloon.alloonserver.service.mission

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.service.mission.dto.CreateMissionDto
import com.alloon.alloonserver.service.s3.S3Service
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
    private val missionTemplateImageRepository: MissionTemplateImageRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) {

    @Transactional
    fun createMission(userId: Long, dto: CreateMissionDto): Mission {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val mission = Mission.toEntity(dto)
        val missionMember = MissionMember(user = user, mission = mission)

        missionRepository.save(mission)
        missionMemberRepository.save(missionMember)

        return mission
    }

    fun getAllMissionTemplateImages(now: LocalDateTime): List<String> {
        return missionTemplateImageRepository.findAllImageUrl(now)
    }

    @Transactional
    fun uploadMissionImage(userId: Long, file: MultipartFile?): String {
        return s3Service.uploadFile(file, "users/$userId/missions", UUID.randomUUID().toString())
    }
}