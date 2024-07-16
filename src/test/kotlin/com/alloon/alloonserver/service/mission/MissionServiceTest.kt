package com.alloon.alloonserver.service.mission

import com.alloon.alloonserver.api.controller.mission.request.MissionCreateHashTagRequest
import com.alloon.alloonserver.api.controller.mission.request.MissionCreateMissionRuleRequest
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
import com.alloon.alloonserver.service.mission.dto.MissionServiceCreateMissionDto
import com.amazonaws.services.s3.AmazonS3Client
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class MissionServiceTest(
    @Autowired private val missionService: MissionService,
    @Autowired private val missionTemplateImageRepository: MissionTemplateImageRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val passwordUtility: PasswordUtility
) : AbstractTestContainer(), AbstractMailProperties {

    @MockBean
    private lateinit var amazonS3Client: AmazonS3Client

    @BeforeEach
    fun beforeEach() {
        `when`(amazonS3Client.getUrl(any(), any()))
            .thenReturn(URL("https://localhost:8080/api/image/mission-service"))
    }

    @DisplayName("미션 생성을 하면 정상 작동한다")
    @Test
    fun givenValid_whenCreateMission_thenReturn() {
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
                    .extracting(
                        "missionName", "description", "goal", "imageUrl", "currentMemberCnt",
                        "missionCreator.username", "missionCreator.imageUrl", "startDate", "endDate"
                    )
                    .containsExactly(
                        request.missionName, request.missionDescription, request.missionGoal,
                        request.missionImageUrl, 1, user.username, user.imageUrl, now, request.missionEndDate
                    )
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

    private fun createValidMissionServiceCreateMissionRequest(): MissionServiceCreateMissionDto {
        return MissionServiceCreateMissionDto(
            "얼른",
            "얼른 프로젝트 설명입니다.",
            "얼른 프로젝트 목표입니다.",
            listOf(MissionCreateMissionRuleRequest("얼른 프로젝트 규칙입니다.")),
            "https://alloon.s3.us-east-2.amazonaws.com/alloon-logo.png",
            LocalDate.of(2025, 1, 1),
            listOf(MissionCreateHashTagRequest("해시"), MissionCreateHashTagRequest("태그"))
        )
    }

    private fun createAndSaveMissionTemplateImage(now: LocalDateTime): MissionTemplateImage {
        return missionTemplateImageRepository.save(
            MissionTemplateImage(
                imageUrl = "image",
                startDateTime = now.minusSeconds(1),
                endDateTime = now.plusSeconds(1),
                sort = 1,
                admin = null
            )
        )
    }

    private fun createAndSaveUserWithContact(): User {
        val contact = contactRepository.save(
            Contact(
                email = "tester@alloon.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        return userRepository.save(
            User(
                contact = contact,
                username = "tester",
                password = encryptedPassword,
                imageUrl = ""
            )
        )
    }
}