package com.alloon.alloonserver.api.service.mission

import com.alloon.alloonserver.api.service.mission.request.MissionServiceCreateMissionRequest
import com.alloon.alloonserver.api.service.mission.response.MissionCreateResponse
import com.alloon.alloonserver.api.service.s3.S3Service
import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.domain.user.UserRepository
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

    /**
     * 생성된 챌린지 정보를 포함한 응답을 반환한다.
     *
     * @param userId 사용자 id
     * @param request 유효성 검사가 포함된 챌린지 생성 폼 데이터
     * @return 생성된 챌린지 정보를 포함한 응답
     * @throws CustomException 사용자 정보를 찾을 수 없을 때 발생한다 ([USER_NOT_FOUND] 404)
     */
    @Transactional
    fun createMission(userId: Long, @Valid request: MissionServiceCreateMissionRequest): MissionCreateResponse {
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

    /**
     * 모든 챌린지 템플릿 이미지 리스트를 반환한다.
     *
     * @param now 현재 시간
     * @return 모든 챌린지 템플릿 이미지 URL 리스트
     */
    fun getAllMissionTemplateImages(now: LocalDateTime): List<String> {
        return missionTemplateImageRepository.findAllImageUrl(now)
    }

    /**
     * S3에 업로드된 이미지 URL을 반환한다.
     *
     * @param userId 사용자 id
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지 URL
     */
    @Transactional
    fun uploadMissionImage(userId: Long, file: MultipartFile?): String {
        return s3Service.uploadFile(file, "users/$userId/missions", UUID.randomUUID().toString())
    }
}