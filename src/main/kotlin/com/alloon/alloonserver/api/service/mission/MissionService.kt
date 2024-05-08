package com.alloon.alloonserver.api.service.mission

import com.alloon.alloonserver.api.service.mission.request.MissionServiceCreateMissionRequest
import com.alloon.alloonserver.api.service.mission.response.MissionCreateResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.domain.user.UserRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime

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
) {

    /**
     * 미션 생성
     * @param userId 회원 식별자
     * @param request 미션 생성 요청
     * @throws USER_NOT_FOUND 404
     * @return 미션 생성 응답
     */
    @Transactional
    fun createMission(userId: Long, @Valid request: MissionServiceCreateMissionRequest): MissionCreateResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val missionMember = request.toMissionMember(user)
        missionRepository.save(missionMember.mission)
        missionMemberRepository.save(missionMember)

        val missionRules = request.toMissionRule(missionMember.mission)
        missionRules.isNotEmpty().let { missionRuleRepository.saveAll(missionRules) }

        val missionHashtags = request.toMissionHashtag(missionMember.mission)
        hashtagRepository.saveAll(missionHashtags.map { it.hashtag })
        missionHashtagRepository.saveAll(missionHashtags)

        return MissionCreateResponse(missionMember, missionRules.map { it.rule }, missionHashtags.map { it.hashtag })
    }

    /**
     * 미션 에시 이미지 전체 조회
     * @param now 현재 시간
     * @return 전체 미션 에시 이미지
     */
    fun getAllMissionTemplateImages(now: LocalDateTime): List<String> {
        return missionTemplateImageRepository.findAllImageUrl(now)
    }
}