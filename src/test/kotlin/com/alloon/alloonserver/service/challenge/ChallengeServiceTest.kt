package com.alloon.alloonserver.service.challenge

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.constant.SortTypeConstants
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.challenge.ChallengeMember
import com.alloon.alloonserver.domain.challenge.ChallengeMemberRepository
import com.alloon.alloonserver.domain.challenge.ChallengeRepository
import com.alloon.alloonserver.domain.feed.Feed
import com.alloon.alloonserver.domain.feed.FeedComment
import com.alloon.alloonserver.domain.feed.FeedCommentRepository
import com.alloon.alloonserver.domain.feed.FeedRepository
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.AbstractMailProperties
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.*
import com.alloon.alloonserver.service.s3.FolderType.CHALLENGES
import com.alloon.alloonserver.service.s3.FolderType.FEEDS
import com.alloon.alloonserver.service.s3.S3Service
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.SliceImpl
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
    private val userRepository = mockk<UserRepository>()
    private val feedRepository = mockk<FeedRepository>()
    private val feedCommentRepository = mockk<FeedCommentRepository>()
    private val s3Service = mockk<S3Service>()
    private val hashtagService = mockk<HashtagService>()

    private val challengeService = ChallengeService(
        challengeRepository,
        challengeMemberRepository,
        userRepository,
        feedRepository,
        feedCommentRepository,
        s3Service,
        hashtagService,
    )

    @DisplayName("챌린지 생성을 하면 정상 작동한다")
    @Test
    fun givenValid_whenCreateChallenge_thenReturn() {
        // given
        val dto = getCreateChallengeDto()
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = dto.toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val multipartFile = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        every { userRepository.find(any()) } returns user
        every { s3Service.uploadImage(any(), any()) } returns ""
        every { s3Service.getImageUrl(any()) } returns imageUrl
        every { challengeRepository.save(any()) } returns challenge
        every { challengeMemberRepository.save(any()) } returns challengeMember
        every { hashtagService.addHashtags(any()) } just Runs

        // when
        val result = challengeService.createChallenge(1L, dto, multipartFile)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("존재하지 않은 회원으로 챌린지 생성을 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUser_whenCreateChallenge_thenThrow() {
        // given
        val userId = 1L
        val dto = getCreateChallengeDto()
        val multipartFile = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.createChallenge(userId, dto, multipartFile) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("챌린지 예시 이미지 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetChallengeExampleImages_thenReturn() {
        // given
        every { s3Service.getChallengeExampleImages() } returns listOf("https://url.kr/5MhHhD")

        // when
        val response = challengeService.getChallengeExampleImages()

        // then
        assertThat(response.size).isEqualTo(1)
    }

    @DisplayName("지금 인기있는 챌린지 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFindPopularChallenges_thenReturn() {
        // given
        val dto = getFindPopularChallengesDto()

        every { challengeRepository.findPopular() } returns listOf(dto, dto, dto, dto)

        // when
        val result = challengeService.findPopularChallenges()

        // then
        assertThat(result.size).isEqualTo(4)
    }

    @DisplayName("챌린지 멤버가 챌린지 소개 조회를 하면 일치하는 챌린지 소개를 반환한다")
    @Test
    fun givenValid_whenFindChallengeInfo_thenReturn() {
        // given
        val challengeId = 1L
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
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

        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val challengeMember = ChallengeMember(user = getUser(), challenge = challenge)

        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember

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

    @DisplayName("모든 챌린지 조회를 하면 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindAllChallenges_thenReturn() {
        // given
        val dto = getFindChallengesDto()
        val content = listOf(dto, dto, dto)
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { challengeRepository.findAllOrderByStartDate(any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val result = challengeService.findAllChallenges(pageable)

        // then
        assertThat(result.content.size).isEqualTo(3)
    }

    @DisplayName("챌린지 개별 조회를 하면 일치하는 챌린지를 반환한다.")
    @Test
    fun givenValid_whenFindAllChallenge_thenReturn() {
        // given
        val challengeId = 1L
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val dto = getFindChallengeDto()

        every { challengeRepository.findInfoById(any()) } returns challenge
        every { challengeMemberRepository.findImagesByChallengeId(any()) } returns listOf(
            ChallengeMemberImageDto("https://url.kr/5MhHhD"),
            ChallengeMemberImageDto("https://url.kr/5MhHhD"),
            ChallengeMemberImageDto("https://url.kr/5MhHhD")
        )

        // when
        val result = challengeService.findChallenge(challengeId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("파티장이 챌린지 수정을 하면 챌린지가 수정된다.")
    @Test
    fun givenValid_whenUpdateChallenge_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val dto = getUpdateChallengeDto()
        val multipartFile = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
        val imageUrl = "https://url.kr/5MhHhD2345"
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val challengeMember = ChallengeMember(user = getUser(), challenge = challenge)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every { s3Service.deleteImage(any(), any()) } just Runs
        every { s3Service.uploadImage(any(), any()) } returns ""
        every { s3Service.getImageUrl(any()) } returns imageUrl
        every { hashtagService.updateHashtags(any()) } just Runs

        // when
        challengeService.updateChallenge(userId, challengeId, dto, multipartFile)

        // then
        assertThat(challenge.name).isEqualTo(dto.name)
        assertThat(challenge.goal).isEqualTo(dto.goal)
        assertThat(challenge.proveTime).isEqualTo(dto.proveTime)
        assertThat(challenge.endDate).isEqualTo(dto.endDate)
        assertThat(challenge.rules[0].rule).isEqualTo(dto.rules[0].rule)
        assertThat(challenge.hashtags[0].hashtag).isEqualTo(dto.hashtags[0].hashtag)
    }

    @DisplayName("챌린지 파티원이 2명 이상일 때 챌린지를 탈퇴하면, 챌린지 멤버에서 삭제되고 멤버수가 감소한다.")
    @Test
    fun givenMultipleMembers_whenDeleteChallenge_thenDeleteChallengeMemberAndDecreaseMemberCnt() {
        // given
        val userId = 1L
        val challengeId = 1L
        val challenge = getCreateChallengeDto()
            .toEntity("https://url.kr/5MhHhD", "ABC12")
            .apply { currentMemberCnt = 3 }
        val challengeMember = ChallengeMember(user = getUser(), challenge = challenge)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every { challengeMemberRepository.delete(any()) } just Runs
        every { hashtagService.deleteHashtags(any()) } just Runs

        // when
        challengeService.deleteChallenge(userId, challengeId)

        // then
        verify { challengeMemberRepository.delete(challengeMember) }
        verify(exactly = 0) { challengeRepository.deleteById(challengeId) }
        verify(exactly = 0) { s3Service.deleteImage(challenge.imageUrl, CHALLENGES) }
        assertThat(challenge.currentMemberCnt).isEqualTo(2)
    }

    @DisplayName("마지막 파티원이 챌린지를 탈퇴하면, 챌린지 멤버에서 삭제되고 해당 챌린지는 삭제된다.")
    @Test
    fun givenLastMember_whenDeleteChallenge_thenDeleteChallengeMemberAndChallenge() {
        // given
        val userId = 1L
        val challengeId = 1L
        val challenge = getCreateChallengeDto()
            .toEntity("https://url.kr/5MhHhD", "ABC12")
            .apply { currentMemberCnt = 1 }
        val challengeMember = ChallengeMember(user = getUser(), challenge = challenge)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every { challengeMemberRepository.delete(any()) } just Runs
        every { challengeRepository.deleteById(any()) } just Runs
        every { s3Service.deleteImage(any(), any()) } just Runs
        every { hashtagService.deleteHashtags(any()) } just Runs

        // when
        challengeService.deleteChallenge(userId, challengeId)

        // then
        verify { challengeMemberRepository.delete(challengeMember) }
        verify { challengeRepository.deleteById(challengeId) }
        verify { s3Service.deleteImage(challenge.imageUrl, CHALLENGES) }
    }

    @DisplayName("챌린지 피드 인증을 하면 피드가 저장되고, 사용자의 피드 인증 횟수가 업데이트된다.")
    @Test
    fun givenChallengeMember_whenCreateChallengeFeed_thenSaveFeedAndUpdateFeedCnt() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val multipartFile = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
        val feed =
            Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl)

        every { userRepository.find(any()) } returns user
        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every {
            feedRepository.existsByChallengeMemberAndCreateDateTimeBetween(any(), any(), any())
        } returns false
        every { s3Service.uploadImage(any(), any(), any()) } returns ""
        every { s3Service.getImageUrl(any()) } returns imageUrl
        every { feedRepository.save(any()) } returns feed

        // when
        challengeService.createChallengeFeed(1L, 1L, multipartFile)

        // then
        assertThat(user.feedCnt).isEqualTo(1)
    }

    @DisplayName("챌린지 파티원이 이미 오늘 챌린지 피드 인증을 했으면 예외가 발생한다.")
    @Test
    fun givenChallengeMemberExistingFeed_whenCreateChallengeFeed_thenThrow() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val multipartFile = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        every { userRepository.find(any()) } returns user
        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every {
            feedRepository.existsByChallengeMemberAndCreateDateTimeBetween(any(), any(), any())
        } returns true

        // when & then
        assertThatThrownBy { challengeService.createChallengeFeed(1L, 1L, multipartFile) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(EXISTING_FEED)
    }

    @DisplayName("챌린지 피드 삭제를 하면 연관된 피드, 댓글이 모두 삭제되고, 사용자의 피드 인증 횟수가 감소한다.")
    @Test
    fun givenValid_whenDeleteChallengeFeed_thenDeleteFeedAndCommentsAndLike() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val imageUrl = "https://url.kr/5MhHhD"
        val user = getUser()
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val feed = Feed(1L, challengeMember, challenge, imageUrl, 1)
        val feedComments = listOf(
            FeedComment(1L, challengeMember, feed, "피드 댓글"),
            FeedComment(2L, challengeMember, feed, "피드 댓글")
        )

        every { userRepository.find(any()) } returns user
        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every { feedRepository.findByIdAndChallengeMemberId(any(), any()) } returns feed
        every { feedCommentRepository.findAllByFeedId(any()) } returns feedComments
        every { feedCommentRepository.deleteAllInBatch(any()) } just Runs
        every { feedRepository.delete(any()) } just Runs
        every { s3Service.deleteImage(any(), any(), any()) } just Runs

        // when
        challengeService.deleteChallengeFeed(userId, challengeId, feedId)

        // then
        verify { feedCommentRepository.deleteAllInBatch(feedComments) }
        verify { feedRepository.delete(feed) }
        verify { s3Service.deleteImage(feed.imageUrl, FEEDS, challengeId) }
        assertThat(user.feedCnt).isEqualTo(0)
    }

    @DisplayName("등록되지 않은 사용자로 피드 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenDeleteChallengeFeed_thenThrow() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L

        every { userRepository.find(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.deleteChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 챌린지의 피드 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallenge_whenDeleteChallengeFeed_thenThrow() {
        // given
        val user = getUser()
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L

        every { userRepository.find(any()) } returns user
        every { challengeRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.deleteChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 챌린지 파티원이 피드 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallengeMember_whenDeleteChallengeFeed_thenThrow() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L

        every { userRepository.find(any()) } returns user
        every { challengeRepository.findInfoById(any()) } returns challenge
        every { challengeMemberRepository.findByUserIdAndChallengeId(any(), any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.deleteChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 피드 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundFeed_whenDeleteChallengeFeed_thenThrow() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L

        every { userRepository.find(any()) } returns user
        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every { feedRepository.findByIdAndChallengeMemberId(any(), any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.deleteChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_NOT_FOUND)
    }

    @DisplayName("챌린지 피드 조회를 하면 정렬 기준으로 정렬된 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindChallengeFeeds_thenReturn() {
        // given
        val challengeId = 1L
        val sort = SortTypeConstants.LATEST
        val dto = getFindChallengeFeedsDto()
        val content = listOf(
            Pair(LocalDate.of(2024, 10, 9), listOf(dto, dto, dto)),
            Pair(LocalDate.of(2024, 10, 8), listOf(dto, dto, dto))
        )
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { feedRepository.findAllByChallengeId(any(), any(), any()) } returns
                SliceImpl(content, pageable, hasNext)

        // when
        val result = challengeService.findChallengeFeeds(challengeId, pageable, sort)

        // then
        assertThat(result.content.size).isEqualTo(2)
    }

    @DisplayName("챌린지 피드 댓글 등록을 하면 피드 댓글이 저장된다.")
    @Test
    fun givenValid_whenCreateChallengeFeedComment_thenReturn() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val feed =
            Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl)
        val comment = "피드 댓글"
        val dto = CreateChallengeFeedCommentDto(comment)
        val feedComment = dto.toEntity(challengeMember, feed)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { feedRepository.findByFeedId(any()) } returns feed
        every { feedCommentRepository.save(any()) } returns feedComment

        // when
        challengeService.createChallengeFeedComment(userId, challengeId, feedId, dto)

        // then
        assertThat(feedComment.comment).isEqualTo(comment)
    }

    @DisplayName("등록되지 않은 챌린지에 피드 댓글 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallenge_whenCreateChallengeFeedComment_thenThrow() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val comment = "피드 댓글"
        val dto = CreateChallengeFeedCommentDto(comment)

        every { challengeRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy {
            challengeService.createChallengeFeedComment(userId, challengeId, feedId, dto)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 챌린지 파티원이 댓글 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallengeMember_whenCreateChallengeFeedComment_thenThrow() {
        // given
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val comment = "피드 댓글"
        val dto = CreateChallengeFeedCommentDto(comment)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns null

        // when & then
        assertThatThrownBy {
            challengeService.createChallengeFeedComment(userId, challengeId, feedId, dto)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 피드에 댓글 등록을 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundFeed_whenCreateChallengeFeedComment_thenThrow() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val comment = "피드 댓글"
        val dto = CreateChallengeFeedCommentDto(comment)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { feedRepository.findByFeedId(any()) } returns null

        // when & then
        assertThatThrownBy {
            challengeService.createChallengeFeedComment(userId, challengeId, feedId, dto)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_NOT_FOUND)
    }

    @DisplayName("챌린지 피드 댓글 삭제를 하면 해당 챌린지 파티원의 댓글이 삭제된다.")
    @Test
    fun givenValid_whenDeleteChallengeFeedComment_thenReturn() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val feed =
            Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl)
        val feedComment = FeedComment(1L, challengeMember, feed, "피드 댓글")
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val commentId = 1L

        every { challengeRepository.findInfoById(any()) } returns challenge
        every { feedRepository.findByFeedId(any()) } returns feed
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { feedCommentRepository.findByCommentId(any()) } returns feedComment
        every { feedCommentRepository.delete(any()) } just Runs

        // when
        challengeService.deleteChallengeFeedComment(userId, challengeId, feedId, commentId)

        // then
        verify { feedCommentRepository.delete(feedComment) }
    }

    @DisplayName("등록되지 않은 챌린지의 피드 댓글 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallenge_whenDeleteChallengeFeedComment_thenThrow() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val commentId = 1L

        every { challengeRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy {
            challengeService.deleteChallengeFeedComment(userId, challengeId, feedId, commentId)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 피드의 댓글 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundFeed_whenDeleteChallengeFeedComment_thenThrow() {
        // given
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val commentId = 1L

        every { challengeRepository.findInfoById(any()) } returns challenge
        every { feedRepository.findByFeedId(any()) } returns null

        // when & then
        assertThatThrownBy {
            challengeService.deleteChallengeFeedComment(userId, challengeId, feedId, commentId)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 챌린지 파티원이 댓글 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallengeMember_whenDeleteChallengeFeedComment_thenThrow() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val feed =
            Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl)
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val commentId = 1L

        every { challengeRepository.findInfoById(any()) } returns challenge
        every { feedRepository.findByFeedId(any()) } returns feed
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns null

        // when & then
        assertThatThrownBy {
            challengeService.deleteChallengeFeedComment(userId, challengeId, feedId, commentId)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 댓글 삭제를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundFeedComment_whenDeleteChallengeFeedComment_thenThrow() {
        // given
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val challenge = getCreateChallengeDto().toEntity(imageUrl, "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val feed =
            Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = imageUrl)
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val commentId = 1L

        every { challengeRepository.findInfoById(any()) } returns challenge
        every { feedRepository.findByFeedId(any()) } returns feed
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { feedCommentRepository.findByCommentId(any()) } returns null

        // when & then
        assertThatThrownBy {
            challengeService.deleteChallengeFeedComment(userId, challengeId, feedId, commentId)
        }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_COMMENT_NOT_FOUND)
    }

    @DisplayName("챌린지 피드 개별 조회를 하면 일치하는 피드를 반환한다.")
    @Test
    fun givenValid_whenFindChallengeFeed_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val user = getUser()
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val dto = getFindChallengeFeedDto()

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { feedRepository.findContentById(any()) } returns dto

        // when
        val result = challengeService.findChallengeFeed(userId, challengeId, feedId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("등록되지 않은 챌린지의 피드를 개별 조회하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallenge_whenFindChallengeFeed_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L

        every { challengeRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.findChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 챌린지 파티원이 피드를 개별 조회하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallengeMember_whenFindChallengeFeed_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns null

        // when & then
        assertThatThrownBy { challengeService.findChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 피드를 개별 조회하면 예외가 발생한다.")
    @Test
    fun givenNotFoundFeed_whenFindChallengeFeed_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val feedId = 1L
        val user = getUser()
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        every { challengeRepository.findInfoById(any()) } returns challenge
        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { feedRepository.findContentById(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.findChallengeFeed(userId, challengeId, feedId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(FEED_NOT_FOUND)
    }

    @DisplayName("챌린지 피드 댓글 리스트 조회를 하면 페이징 객체를 반환한다.")
    @Test
    fun givenValid_whenFindChallengeFeedComments_thenReturn() {
        // given
        val feedId = 1L
        val dto = FindChallengeFeedCommentsDto(1L, "tester", "피드 댓글")
        val content = listOf(dto, dto, dto)
        val pageable = PageRequest.of(0, 10)
        val hasNext = true

        every { feedCommentRepository.findAllByFeedId(any(), any()) } returns SliceImpl(
            content,
            pageable,
            hasNext
        )

        // when
        val result = challengeService.findChallengeFeedComments(feedId, pageable)

        // then
        assertThat(result.content.size).isEqualTo(3)
    }

    @DisplayName("챌린지 초대코드를 조회하면 일치하는 초대코드를 반환한다.")
    @Test
    fun givenValid_whenFindChallengeInvitationCode_thenReturn() {
        // given
        val userId = 1L
        val challengeId = 1L
        val user = getUser()
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)
        val dto = FindChallengeInvitationCodeDto("챌린지 이름", "ABC12")

        every {
            challengeMemberRepository.findByUserIdAndChallengeId(userId, challengeId)
        } returns challengeMember
        every { challengeRepository.findInvitationCodeById(any()) } returns dto

        // when
        val result = challengeService.findChallengeInvitationCode(userId, challengeId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("등록되지 않은 챌린지 파티원이 챌린지 초대코드를 조회하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallengeMember_whenFindChallengeInvitationCode_thenThrow() {
        // given
        val userId = 1L
        val challengeId = 1L

        every { challengeMemberRepository.findByUserIdAndChallengeId(any(), any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.findChallengeInvitationCode(userId, challengeId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_MEMBER_NOT_FOUND)
    }

    @DisplayName("등록되지 않은 챌린지 초대코드를 조회하면 예외가 발생한다.")
    @Test
    fun givenNotFoundChallenge_whenFindChallengeInvitationCode_thenThrow() {
        // given
        val userId = 1L
        val challengeId = 1L
        val user = getUser()
        val challenge = getCreateChallengeDto().toEntity("https://url.kr/5MhHhD", "ABC12")
        val challengeMember = ChallengeMember(user = user, challenge = challenge)

        every {
            challengeMemberRepository.findByUserIdAndChallengeId(any(), any())
        } returns challengeMember
        every { challengeRepository.findInvitationCodeById(any()) } returns null

        // when & then
        assertThatThrownBy { challengeService.findChallengeInvitationCode(userId, challengeId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(CHALLENGE_NOT_FOUND)
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }

    private fun getCreateChallengeDto(): CreateChallengeDto {
        return CreateChallengeDto(
            name = "챌린지 이름",
            isPublic = true,
            goal = "챌린지 목표입니다.",
            proveTime = LocalTime.of(13, 0),
            endDate = LocalDate.of(2024, 12, 1),
            rules = listOf(
                ChallengeRuleDto("챌린지 인증 룰1"),
                ChallengeRuleDto("챌린지 인증 룰2"),
                ChallengeRuleDto("챌린지 인증 룰3"),
            ),
            hashtags = listOf(
                ChallengeHashtagDto("해시태그 1"),
                ChallengeHashtagDto("해시태그 2"),
            ),
            imageUrl = "https://url.kr/5MhHhD"
        )
    }

    private fun getFindChallengeInfoDto(): FindChallengeInfoDto {
        return FindChallengeInfoDto(
            listOf(
                ChallengeRuleDto("챌린지 인증 룰1"),
                ChallengeRuleDto("챌린지 인증 룰2"),
                ChallengeRuleDto("챌린지 인증 룰3"),
            ),
            LocalTime.of(13, 0),
            "챌린지 목표입니다.",
            LocalDate.now(),
            LocalDate.of(2024, 12, 1),
        )
    }

    private fun getFindChallengesDto(): FindChallengesDto {
        return FindChallengesDto(
            1L,
            "챌린지 이름",
            LocalDate.of(2024, 12, 1),
            "https://url.kr/5MhHhD",
            listOf(
                FindChallengeHashtagDto(1L, "해시태그 1"),
                FindChallengeHashtagDto(1L, "해시태그 2"),
            )
        )
    }

    private fun getFindPopularChallengesDto(): FindPopularChallengesDto {
        return FindPopularChallengesDto(
            1L,
            "챌린지 이름",
            "https://url.kr/5MhHhD",
            "챌린지 목표입니다.",
            3,
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                FindChallengeHashtagDto(1L, "해시태그 1"),
                FindChallengeHashtagDto(1L, "해시태그 2"),
            ),
            listOf("https://url.kr/5MhHhD", "https://url.kr/5MhHhD", "https://url.kr/5MhHhD")
        )
    }

    private fun getFindChallengeDto(): FindChallengeDto {
        return FindChallengeDto(
            "챌린지 이름",
            "챌린지 목표입니다.",
            "https://url.kr/5MhHhD",
            1,
            true,
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                ChallengeRuleDto("챌린지 인증 룰1"),
                ChallengeRuleDto("챌린지 인증 룰2"),
                ChallengeRuleDto("챌린지 인증 룰3"),
            ),
            listOf(
                ChallengeHashtagDto("해시태그 1"),
                ChallengeHashtagDto("해시태그 2"),
            ),
            listOf(
                ChallengeMemberImageDto("https://url.kr/5MhHhD"),
                ChallengeMemberImageDto("https://url.kr/5MhHhD"),
                ChallengeMemberImageDto("https://url.kr/5MhHhD"),
            )
        )
    }

    private fun getUpdateChallengeDto(): UpdateChallengeDto {
        return UpdateChallengeDto(
            "챌린지 이름!!",
            "챌린지 목표입니다!!",
            LocalTime.of(11, 0),
            LocalDate.of(2024, 12, 1),
            listOf(
                ChallengeRuleDto("챌린지 인증 룰4"),
                ChallengeRuleDto("챌린지 인증 룰5"),
                ChallengeRuleDto("챌린지 인증 룰6"),
            ),
            listOf(
                ChallengeHashtagDto("해시태그 3"),
                ChallengeHashtagDto("해시태그 4"),
            ),
        )
    }

    private fun getFindChallengeFeedsDto(): FindChallengeFeedsDto {
        return FindChallengeFeedsDto(
            1L,
            "tester",
            "https://url.kr/5MhHhD",
            LocalDateTime.now(),
            LocalTime.of(13, 0),
        )
    }

    private fun getFindChallengeFeedDto(): FindChallengeFeedDto {
        return FindChallengeFeedDto(
            "tester",
            "https://url.kr/5MhHhD",
            "https://url.kr/5MhHhD",
            LocalDateTime.now(),
            10
        )
    }
}