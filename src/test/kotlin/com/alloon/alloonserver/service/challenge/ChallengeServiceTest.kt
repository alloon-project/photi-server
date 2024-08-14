package com.alloon.alloonserver.service.challenge

import com.alloon.alloonserver.common.constant.ExceptionCode.CHALLENGE_NOT_FOUND
import com.alloon.alloonserver.common.constant.ExceptionCode.CHALLENGE_MEMBER_NOT_FOUND
import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.*
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.AbstractMailProperties
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.*
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
class ChallengeServiceTest : AbstractMailProperties {

    private val challengeRepository = mockk<ChallengeRepository>()
    private val challengeMemberRepository = mockk<ChallengeMemberRepository>()
    private val challengeTemplateImageRepository = mockk<ChallengeTemplateImageRepository>()
    private val userRepository = mockk<UserRepository>()
    private val s3Service = mockk<S3Service>()

    private val challengeService = ChallengeService(
        challengeRepository,
        challengeMemberRepository,
        challengeTemplateImageRepository,
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

    @DisplayName("챌린지 생성을 하면 정상 작동한다")
    @Test
    fun givenValid_whenCreateChallenge_thenReturn() {
        // given
        mockkObject(Challenge)
        val dto = getCreateChallengeDto()
        val user = getUser()
        val challenge = Challenge.toEntity(dto)
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        every { userRepository.find(any()) } returns user
        every { Challenge.toEntity(any()) } returns challenge
        every { challengeRepository.save(any()) } returns challenge
        every { challengeMemberRepository.save(any()) } returns challengeMember

        // when
        val result = challengeService.createChallenge(1L, dto)

        // then
        assertThat(result).isEqualTo(challenge)
    }

    @DisplayName("존재하지 않은 회원으로 챌린지 생성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenCreateChallenge_thenThrow() {
        // given
        val userId = 1L
        val dto = getCreateChallengeDto()

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.createChallenge(userId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("챌린 예시 이미지 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetAllChallengeTemplateImages_thenReturn() {
        // given
        val now = LocalDateTime.now()
        val challengeTemplateImage = getChallengeTemplateImage(now)

        every { challengeTemplateImageRepository.findAllImageUrl(any()) } returns mutableListOf("image")

        // when
        val response = challengeService.getAllChallengeTemplateImages(now)

        // then
        assertThat(response).containsExactly(challengeTemplateImage.imageUrl)
    }

    @DisplayName("챌린지 이미지 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadChallengeImage_thenReturn() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        every { s3Service.uploadFile(any(), any(), any()) } returns ""

        // when
        val response = challengeService.uploadChallengeImage(1L, file)

        // then
        assertThat(response).isNotNull()
    }

    @DisplayName("지금 인기있는 챌린지 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindPopularChallenges_thenReturn() {
        // given
        val challenge = FindPopularChallengesDto(
            1L,
            "챌린지 이름",
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf("해시태그 1", "해시태그 2")
        )

        every { challengeRepository.findPopular() } returns listOf(challenge, challenge, challenge, challenge)

        // when
        val result = challengeService.findPopularChallenges()

        // then
        assertThat(result.size).isEqualTo(4)
    }

    @DisplayName("챌린지 멤버가 챌린지 소개 조회를 하면 일치하는 챌린지 소개를 반환한다")
    @Test
    fun givenValid_whenFindChallengeInfo_thenReturn() {
        // given
        mockkObject(Challenge)
        val challengeId = 1L
        val challenge = Challenge.toEntity(getCreateChallengeDto())
        val dto = getFindChallengeInfoDto()

        every { challengeRepository.findInfoById(any()) } returns challenge

        // when
        val result = challengeService.findChallengeInfo(challengeId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("등록되지 않은 챌린지를 찾으려고 하면 예외가 발생한다")
    @Test
    fun givenNotFoundChallenge_whenFindChallengeInfo_thenThrow() {
        // given
        val challengeId = 1L

        every { challengeRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.findChallengeInfo(challengeId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("챌린지 멤버가 개인목표 작성을 하면 개인목표가 수정된다")
    @Test
    fun givenValid_whenUpdateChallengeMemberGoal_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val dto = UpdateChallengeMemberGoalDto("개인목표")

        mockkObject(Challenge)
        val challenge = Challenge.toEntity(getCreateChallengeDto())
        val challengeMember = ChallengeMember(user = getUser(), challenge = challenge)

        every { challengeMemberRepository.findByUserIdAndChallengeId(any(), any()) } returns challengeMember

        // when
        challengeService.updateChallengeMemberGoal(userId, challengeId, dto)

        // then
        assertThat(challengeMember.goal).isEqualTo(dto.goal)
    }

    @DisplayName("등록되지 않은 챌린지 멤버가 개인목표 작성을 하면 예외가 발생한다")
    @Test
    fun givenNotFoundChallengeMember_whenUpdateChallengeMemberGoal_thenThrow() {
        // given
        val userId = 1L
        val challengeId = 1L
        val dto = UpdateChallengeMemberGoalDto("개인목표")

        every { challengeMemberRepository.findByUserIdAndChallengeId(any(), any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.updateChallengeMemberGoal(userId, challengeId, dto) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("챌린지 멤버가 챌린지 파티원 조회를 하면 일치하는 챌린지 멤버 리스트를 반환한다")
    @Test
    fun givenValid_whenFindChallengeMembers_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val dto = FindChallengeMembersDto(1L, "tester", "", true, LocalDateTime.now(), "개인목표")

        every { challengeMemberRepository.findAllByChallengeId(any(), any()) } returns listOf(dto)

        // when
        val result = challengeService.findChallengeMembers(userId, challengeId)

        // then
        assertThat(result[0]).isEqualTo(dto)
        assertThat(result.size).isEqualTo(1)
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }

    private fun getCreateChallengeDto(): CreateChallengeDto {
        return CreateChallengeDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf(
                CreateChallengeRuleDto("챌린지 인증 룰1"),
                CreateChallengeRuleDto("챌린지 인증 룰2"),
                CreateChallengeRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                CreateChallengeHashtagDto("해시태그 1"),
                CreateChallengeHashtagDto("해시태그 2"),
            )
        )
    }

    private fun getChallengeTemplateImage(now: LocalDateTime): ChallengeTemplateImage {
        return ChallengeTemplateImage(
            imageUrl = "image",
            startDateTime = now.minusSeconds(1),
            endDateTime = now.plusSeconds(1),
            admin = null
        )
    }

    private fun getFindChallengeInfoDto(): FindChallengeInfoDto {
        return FindChallengeInfoDto(
            listOf(
                CreateChallengeRuleDto("챌린지 인증 룰1"),
                CreateChallengeRuleDto("챌린지 인증 룰2"),
                CreateChallengeRuleDto("챌린지 인증 룰3"),
            ),
            LocalTime.of(13, 0),
            "챌린지 목표입니다.",
            LocalDate.now(),
            LocalDate.of(2024, 12, 1),
        )
    }
}