package com.alloon.alloonserver.service.mission

import com.alloon.alloonserver.common.constant.ExceptionCode.MISSION_NOT_FOUND
import com.alloon.alloonserver.common.constant.ExceptionCode.MISSION_MEMBER_NOT_FOUND
import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.mission.*
import com.alloon.alloonserver.service.mission.dto.FindPopularMissionsDto
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.AbstractMailProperties
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.mission.dto.CreateMissionDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionHashtagDto
import com.alloon.alloonserver.service.mission.dto.CreateMissionRuleDto
import com.alloon.alloonserver.service.mission.dto.FindMissionInfoDto
import com.alloon.alloonserver.service.mission.dto.UpdateMissionMemberGoalDto
import com.alloon.alloonserver.service.s3.S3Service
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Transactional
@ActiveProfiles("test")
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class MissionServiceTest : AbstractMailProperties {

    private val missionRepository = mockk<MissionRepository>()
    private val missionMemberRepository = mockk<MissionMemberRepository>()
    private val missionTemplateImageRepository = mockk<MissionTemplateImageRepository>()
    private val userRepository = mockk<UserRepository>()
    private val s3Service = mockk<S3Service>()

    private val missionService = MissionService(
        missionRepository,
        missionMemberRepository,
        missionTemplateImageRepository,
        userRepository,
        s3Service
    )

    @BeforeEach
    fun beforeEach() {
        unmockkAll()
    }

//    @MockBean
//    private lateinit var amazonS3Client: AmazonS3Client
//
//    @BeforeEach
//    fun beforeEach() {
//        `when`(amazonS3Client.getUrl(any(), any()))
//            .thenReturn(URL("https://localhost:8080/api/image/mission-service"))
//    }

    @DisplayName("미션 생성을 하면 정상 작동한다")
    @Test
    fun givenValid_whenCreateMission_thenReturn() {
        // given
        mockkObject(Mission)
        val dto = getCreateMissionDto()
        val user = getUser()
        val mission = Mission.toEntity(dto)
        val missionMember = MissionMember(user = user, mission = mission)

        every { userRepository.find(any()) } returns user
        every { Mission.toEntity(any()) } returns mission
        every { missionRepository.save(any()) } returns mission
        every { missionMemberRepository.save(any()) } returns missionMember

        // when
        val result = missionService.createMission(1L, dto)

        // then
        assertThat(result).isEqualTo(mission)
    }

    @DisplayName("존재하지 않은 회원으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenCreateMission_thenThrow() {
        // given
        val userId = 1L
        val dto = getCreateMissionDto()

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { missionService.createMission(userId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("미션 예시 이미지 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAllMissionTemplateImages_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val missionTemplateImage = getMissionTemplateImage(now)

        every { missionTemplateImageRepository.findAllImageUrl(any()) } returns mutableListOf("image")

        // when
        val response = missionService.getAllMissionTemplateImages(now)

        // then
        assertThat(response).containsExactly(missionTemplateImage.imageUrl)
    }

    @DisplayName("미션 이미지 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadMissionImage_thenReturn() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        every { s3Service.uploadFile(any(), any(), any()) } returns ""

        // when
        val response = missionService.uploadMissionImage(1L, file)

        // then
        assertThat(response).isNotNull()
    }

    @DisplayName("지금 인기있는 챌린지 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindPopularMissions_thenReturn() {
        // given
        val mission = FindPopularMissionsDto(
            1L,
            "챌린지 이름",
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf("해시태그 1", "해시태그 2")
        )

        every { missionRepository.findPopular() } returns listOf(mission, mission, mission, mission)

        // when
        val result = missionService.findPopularMissions()

        // then
        assertThat(result.size).isEqualTo(4)
    }

    @DisplayName("챌린지 멤버가 챌린지 소개 조회를 하면 일치하는 챌린지 소개를 반환한다")
    @Test
    fun givenValid_whenFindMissionInfo_thenReturn() {
        // given
        mockkObject(Mission)
        val missionId = 1L
        val mission = Mission.toEntity(getCreateMissionDto())
        val dto = getFindMissionInfoDto()

        every { missionRepository.findInfoById(any()) } returns mission

        // when
        val result = missionService.findMissionInfo(missionId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("등록되지 않은 챌린지를 찾으려고 하면 예외가 발생한다")
    @Test
    fun givenNotFoundMission_whenFindMissionInfo_thenThrow() {
        // given
        val missionId = 1L

        every { missionRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy { missionService.findMissionInfo(missionId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(MISSION_NOT_FOUND)
    }

    @DisplayName("챌린지 멤버가 개인목표 작성을 하면 개인목표가 수정된다")
    @Test
    fun givenValid_whenUpdateMissionMemberGoal_thenReturn() {
        // given
        val userId = 1L
        val missionId = 1L
        val dto = UpdateMissionMemberGoalDto("개인목표")

        mockkObject(Mission)
        val mission = Mission.toEntity(getCreateMissionDto())
        val missionMember = MissionMember(user = getUser(), mission = mission)

        every { missionMemberRepository.findByUserIdAndMissionId(any(), any()) } returns missionMember

        // when
        missionService.updateMissionMemberGoal(userId, missionId, dto)

        // then
        assertThat(missionMember.goal).isEqualTo(dto.goal)
    }

    @DisplayName("등록되지 않은 챌린지 멤버가 개인목표 작성을 하면 예외가 발생한다")
    @Test
    fun givenNotFoundMissionMember_whenUpdateMissionMemberGoal_thenThrow() {
        // given
        val userId = 1L
        val missionId = 1L
        val dto = UpdateMissionMemberGoalDto("개인목표")

        every { missionMemberRepository.findByUserIdAndMissionId(any(), any()) } returns null

        // when & then
        assertThatThrownBy { missionService.updateMissionMemberGoal(userId, missionId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(MISSION_MEMBER_NOT_FOUND)
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }

    private fun getCreateMissionDto(): CreateMissionDto {
        return CreateMissionDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf(
                CreateMissionRuleDto("챌린지 인증 룰1"),
                CreateMissionRuleDto("챌린지 인증 룰2"),
                CreateMissionRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                CreateMissionHashtagDto("해시태그 1"),
                CreateMissionHashtagDto("해시태그 2"),
            )
        )
    }

    private fun getMissionTemplateImage(now: LocalDateTime): MissionTemplateImage {
        return MissionTemplateImage(
            imageUrl = "image",
            startDateTime = now.minusSeconds(1),
            endDateTime = now.plusSeconds(1),
            admin = null
        )
    }

    private fun getFindMissionInfoDto(): FindMissionInfoDto {
        return FindMissionInfoDto(
            listOf(
                CreateMissionRuleDto("챌린지 인증 룰1"),
                CreateMissionRuleDto("챌린지 인증 룰2"),
                CreateMissionRuleDto("챌린지 인증 룰3"),
            ),
            LocalTime.of(13, 0),
            "챌린지 목표입니다.",
            LocalDate.now(),
            LocalDate.of(2024, 12, 1),
        )
    }
}