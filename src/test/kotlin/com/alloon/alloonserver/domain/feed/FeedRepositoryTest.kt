package com.alloon.alloonserver.domain.feed

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.challenge.*
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeHashtagDto
import com.alloon.alloonserver.service.challenge.dto.CreateChallengeRuleDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class FeedRepositoryTest(
    @Autowired private val feedRepository: FeedRepository,
    @Autowired private val challengeRepository: ChallengeRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val challengeMemberRepository: ChallengeMemberRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @DisplayName("피드 식별자로 피드 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturnTrue() {
        // given
        val feed = createAdnSaveFeed()

        // when
        val result = feedRepository.find(feed.id!!)

        // then
        assertThat(result).isEqualTo(feed)
    }

    private fun createAdnSaveFeed(): Feed {
        val challenge = createAndSaveChallenge()
        val contact = contactRepository.save(
            Contact(
                email = "tester@alloon.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )
        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(
            User(
                contact = contact,
                username = "tester",
                password = encryptedPassword,
                imageUrl = ""
            )
        )
        val challengeMember =
            challengeMemberRepository.save(ChallengeMember(user = user, challenge = challenge, isCreator = true))

        return feedRepository.save(Feed(challengeMember = challengeMember, challenge = challenge, imageUrl = ""))
    }

    private fun createAndSaveChallenge(): Challenge {
        val dto = getCreateChallengeDto()
        val challenge = Challenge.toEntity(dto)
        return challengeRepository.save(challenge)
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
}