package com.alloon.alloonserver.domain.challenge

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
class ChallengeMemberRepositoryTest(
    @Autowired private val challengeMemberRepository: ChallengeMemberRepository,
    @Autowired private val challengeRepository: ChallengeRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
) {

    @DisplayName("챌린지 멤버 식별자로 챌린지 멤버 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturnTrue() {
        // given
        val challengeMember = createAndSaveChallengeMember()

        // when
        val result = challengeMemberRepository.find(challengeMember.id!!)

        // then
        assertThat(result).isEqualTo(result)
    }

    @DisplayName("사용자 id와 챌린지 id로 조회하면 일치하는 챌린지 멤버를 반환한다")
    @Test
    fun givenValid_whenFindByUserIdAndChallengeId_thenReturnChallengeMember() {
        // given
        val challengeMember = createAndSaveChallengeMember()
        val userId = challengeMember.user?.id
        val challengeId = challengeMember.challenge.id

        // when
        val result = userId?.let { uid ->
            challengeId?.let { mid ->
                challengeMemberRepository.findByUserIdAndChallengeId(uid, mid)
            }
        }

        // then
        assertThat(result).isEqualTo(challengeMember)
    }

    @DisplayName("챌린지 id로 조회하면 정렬된 챌린지 멤버 리스트를 반환한다")
    @Test
    fun givenValid_whenFindAllByChallengeId_thenReturnChallengeMembers() {
        // given
        val tester = createAndSaveChallengeMember()
        val challenge = tester.challenge.id?.let { challengeRepository.findById(it) }?.get()

        val contact = contactRepository.save(getContact("tester2@photi.com"))
        val user = userRepository.save(getUser(contact, "tester2"))
        val tester2 = challenge?.let { ChallengeMember(user = user, challenge = it, isCreator = false) }

        val challengeMember = tester2?.let { challengeMemberRepository.save(it) }
        val userId = challengeMember?.user?.id
        val challengeId = challengeMember?.challenge?.id

        // when
        val result = userId?.let { uid ->
            challengeId?.let { mid ->
                challengeMemberRepository.findAllByChallengeId(uid, mid)
            }
        }

        // then
        assertThat(result?.size).isEqualTo(2)
    }

    private fun getContact(email: String): Contact {
        return Contact(email = email, verificationCode = "000000", verifyYn = true)
    }

    private fun getUser(contact: Contact, username: String): User {
        return User(contact = contact, username = username, password = "password1!", imageUrl = "")
    }

    private fun createAndSaveChallengeMember(): ChallengeMember {
        val challenge = createAndSaveChallenge()
        val contact = contactRepository.save(getContact("tester@photi.com"))
        val user = userRepository.save(getUser(contact, "tester"))
        return challengeMemberRepository.save(ChallengeMember(user = user, challenge = challenge))
    }

    private fun createAndSaveChallenge(): Challenge {
        val dto = getCreateChallengeDto()
        val challenge = dto.toEntity( "https://url.kr/5MhHhD")
        return challengeRepository.save(challenge)
    }

    private fun getCreateChallengeDto(): CreateChallengeDto {
        return CreateChallengeDto(
            "챌린지 이름",
            true,
            "챌린지 목표입니다.",
            LocalTime.of(13, 0),
            LocalDate.of(2024, 12, 1),
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