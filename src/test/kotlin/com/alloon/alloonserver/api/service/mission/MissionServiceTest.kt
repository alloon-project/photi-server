package com.alloon.alloonserver.api.service.mission

import com.alloon.alloonserver.api.service.mission.request.MissionCreateHashTagServiceRequest
import com.alloon.alloonserver.api.service.mission.request.MissionCreateMissionRuleServiceRequest
import com.alloon.alloonserver.api.service.mission.request.MissionServiceCreateMissionRequest
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.mission.MissionTemplateImage
import com.alloon.alloonserver.domain.mission.MissionTemplateImageRepository
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.AbstractMailProperties
import com.alloon.alloonserver.framework.AbstractTestContainer
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectResult
import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.MailSender
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class MissionServiceTest (
    @Autowired private val missionService: MissionService,
    @Autowired private val missionTemplateImageRepository: MissionTemplateImageRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val passwordUtility: PasswordUtility
) : AbstractTestContainer(), AbstractMailProperties {

    @MockBean private lateinit var amazonS3Client: AmazonS3Client
    @BeforeEach
    fun beforeEach() {
        `when`(amazonS3Client.getUrl(any(), any()))
            .thenReturn(URL("https://localhost:8080/api/image/mission-service"))
    }

    @DisplayName("미션 생성을 하면 정상 작동한다")
    @Test
    fun givenValid_whenCreateMission_thenReturn(){
        // given
        val user = createAndSaveUserWithContact()
        val now = LocalDate.now()

        val request = createValidMissionServiceCreateMissionRequest()

        // when
        val response = missionService.createMission(user.id!!, request)

        // then
        assertAll(
            { assertThat(response.missionId).isNotNull() },
            {
                assertThat(response)
                    .extracting("missionName", "description", "goal", "imageUrl", "currentMemberCnt",
                        "missionCreator.username", "missionCreator.imageUrl", "startDate", "endDate")
                    .containsExactly(request.missionName, request.missionDescription, request.missionGoal,
                        request.missionImageUrl, 1, user.username, user.imageUrl, now, request.missionEndDate)
            },
            {
                assertThat(response.rules)
                    .extracting("missionRuleId")
                    .isNotNull()
            },
            {
                assertThat(response.rules)
                    .extracting("rule")
                    .isEqualTo(request.missionRules.map { it.missionRule })
            },
            {
                assertThat(response.hashtags)
                    .extracting("hashtagId")
                    .isNotNull()
            },
            {
                assertThat(response.hashtags)
                    .extracting("tag")
                    .isEqualTo(request.hashtags.map { it.hashtag })
            },
        )
    }

    @DisplayName("규칙 없이 미션 생성을 하면 정상 작동한다")
    @Test
    fun givenWithoutMissionRules_whenCreateMission_thenReturn() {
        // given
        val user = createAndSaveUserWithContact()
        val now = LocalDate.now()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionRules = emptyList()

        // when
        val response = missionService.createMission(user.id!!, request)

        // then
        assertAll(
            { assertThat(response.missionId).isNotNull() },
            {
                assertThat(response)
                    .extracting("missionName", "description", "goal", "imageUrl", "currentMemberCnt",
                        "missionCreator.username", "missionCreator.imageUrl", "startDate", "endDate")
                    .containsExactly(request.missionName, request.missionDescription, request.missionGoal,
                        request.missionImageUrl, 1, user.username, user.imageUrl, now, request.missionEndDate)
            },
            {
                assertThat(response.rules).isEmpty()
            },
            {
                assertThat(response.hashtags)
                    .extracting("hashtagId")
                    .isNotNull()
            },
            {
                assertThat(response.hashtags)
                    .extracting("tag")
                    .isEqualTo(request.hashtags.map { it.hashtag })
            },
        )
    }

    @DisplayName("2자 미만인 미션명으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan2MissionName_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionName = "a"

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_NAME_LENGTH_INVALID.message)
    }

    @DisplayName("16자 초과인 미션명으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan30MissionName_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionName = "a".repeat(17)

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_NAME_LENGTH_INVALID.message)
    }

    @DisplayName("10자 미만인 미션 소개로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan1MissionDescription_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionDescription = "a".repeat(9)

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_DESCRIPTION_LENGTH_INVALID.message)
    }

    @DisplayName("120자 초과인 미션 소개로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan500MissionDescription_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionDescription = "a".repeat(121)

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_DESCRIPTION_LENGTH_INVALID.message)
    }

    @DisplayName("1자 미만인 미션 목표로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan1MissionGoal_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionGoal = ""

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_GOAL_LENGTH_INVALID.message)
    }

    @DisplayName("30자 초과인 미션 목표로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan30MissionGoal_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionGoal = "a".repeat(31)

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_GOAL_LENGTH_INVALID.message)
    }

    @DisplayName("5개 초과인 규칙으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan5MissionRules_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionRules = listOf(MissionCreateMissionRuleServiceRequest("a"),
            MissionCreateMissionRuleServiceRequest("a"),
            MissionCreateMissionRuleServiceRequest("a"),
            MissionCreateMissionRuleServiceRequest("a"),
            MissionCreateMissionRuleServiceRequest("a"),
            MissionCreateMissionRuleServiceRequest("a"))

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_RULES_LENGTH_INVALID.message)
    }

    @DisplayName("1자 미만인 규칙으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan1MissionRule_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionRules = listOf(MissionCreateMissionRuleServiceRequest(""))

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_RULE_LENGTH_INVALID.message)
    }

    @DisplayName("30자 초과인 규칙으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan30MissionRule_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionRules = listOf(MissionCreateMissionRuleServiceRequest("a".repeat(31)))

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_RULE_LENGTH_INVALID.message)
    }

    @DisplayName("1자 미만인 미션 대표 이미지로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan1MissionImageUrl_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionImageUrl = ""

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_IMAGE_URL_LENGTH_INVALID.message)
    }

    @DisplayName("500자 초과인 미션 대표 이미지로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan500MissionImageUrl_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.missionImageUrl = "a".repeat(501)

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(MISSION_IMAGE_URL_LENGTH_INVALID.message)
    }

    @DisplayName("1개 미만인 해시태그로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan1Hashtags_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.hashtags = emptyList()

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(HASHTAGS_LENGTH_INVALID.message)
    }

    @DisplayName("5개 초과인 해시태그로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan5Hashtags_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.hashtags = listOf(MissionCreateHashTagServiceRequest("해"),
            MissionCreateHashTagServiceRequest("시"),
            MissionCreateHashTagServiceRequest("태"),
            MissionCreateHashTagServiceRequest("그"),
            MissionCreateHashTagServiceRequest("해"),
            MissionCreateHashTagServiceRequest("시"))

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(HASHTAGS_LENGTH_INVALID.message)
    }

    @DisplayName("1자 미만인 해시태그로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenLessThan1Hashtag_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.hashtags = listOf(MissionCreateHashTagServiceRequest(""))

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(HASHTAG_LENGTH_INVALID.message)
    }

    @DisplayName("30자 초과인 해시태그로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenGreaterThan30Hashtag_whenCreateMission_thenThrow() {
        // given
        val user = createAndSaveUserWithContact()

        val request = createValidMissionServiceCreateMissionRequest()
        request.hashtags = listOf(MissionCreateHashTagServiceRequest("해".repeat(31)))

        // when & then
        assertThatThrownBy { missionService.createMission(user.id!!, request) }
            .isInstanceOf(ConstraintViolationException::class.java)
            .extracting("message")
            .asString()
            .contains(HASHTAG_LENGTH_INVALID.message)
    }

    @DisplayName("존재하지 않은 회원으로 미션 생성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenCreateMission_thenThrow() {
        // given
        val userId = 1L
        val request = createValidMissionServiceCreateMissionRequest()

        // when & then
        assertThatThrownBy { missionService.createMission(userId, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("미션 예시 이미지 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAllMissionTemplateImages_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val missionTemplateImage = createAndSaveMissionTemplateImage(now)

        // when
        val response = missionService.getAllMissionTemplateImages(now)

        // then
        assertThat(response).containsExactly(missionTemplateImage.imageUrl)
    }

    @DisplayName("미션 이미지 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadMissionImage_thenReturn() {
        // given
        val user = createAndSaveUserWithContact()
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        // when
        val response = missionService.uploadMissionImage(user.id!!, file)

        // then
        assertThat(response).isNotNull()
    }

    private fun createValidMissionServiceCreateMissionRequest(): MissionServiceCreateMissionRequest {
        return MissionServiceCreateMissionRequest(
            "얼른",
            "얼른 프로젝트 설명입니다.",
            "얼른 프로젝트 목표입니다.",
            listOf(MissionCreateMissionRuleServiceRequest("얼른 프로젝트 규칙입니다.")),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagServiceRequest("해시"),
                MissionCreateHashTagServiceRequest("태그"))
        )
    }

    private fun createAndSaveMissionTemplateImage(now: LocalDateTime): MissionTemplateImage {
        return missionTemplateImageRepository.save(
            MissionTemplateImage(imageUrl = "image", startDateTime = now.minusSeconds(1),
                endDateTime = now.plusSeconds(1), sort = 1, admin = null)
        )
    }

    private fun createAndSaveUserWithContact(): User {
        val contact = contactRepository.save(Contact(
            email = "tester@alloon.com",
            verificationCode = "000000",
            verifyYn = true
        ))

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        return userRepository.save(User(contact = contact, username = "tester", password = encryptedPassword, imageUrl = ""))
    }
}