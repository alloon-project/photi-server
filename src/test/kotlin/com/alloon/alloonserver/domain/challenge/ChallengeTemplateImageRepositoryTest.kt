package com.alloon.alloonserver.domain.challenge

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.user.*
import com.alloon.alloonserver.framework.TestContainerInitializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class ChallengeTemplateImageRepositoryTest(
    @Autowired private val challengeTemplateImageRepository: ChallengeTemplateImageRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {
    @DisplayName("챌린지 예시 이미지 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturn() {
        // given
        val adminRole = createAndSaveAdminWithContact()

        val now = LocalDateTime.now()

        val challengeTemplateImages: List<ChallengeTemplateImage> = listOf(
            createAndSaveChallengeTemplateImage(
                adminRole.user, "image1", now.minusSeconds(2),
                now.minusSeconds(1), 1
            ),
            createAndSaveChallengeTemplateImage(
                adminRole.user, "image2", now.minusSeconds(2),
                now.plusSeconds(2), 2
            ),
            createAndSaveChallengeTemplateImage(
                adminRole.user, "image3", now.minusSeconds(2),
                now.plusSeconds(2), 3
            ),
            createAndSaveChallengeTemplateImage(
                adminRole.user, "image4", now.minusSeconds(1),
                now.plusSeconds(1), 4
            ),
            createAndSaveChallengeTemplateImage(
                adminRole.user, "image5", now.minusSeconds(1),
                now.plusSeconds(1), 5
            ),
            createAndSaveChallengeTemplateImage(
                adminRole.user, "image6", now.plusSeconds(1),
                now.plusSeconds(2), 6
            ),
        )
        challengeTemplateImages[1].serviceStatus = ServiceStatus.ADMIN_DEL
        challengeTemplateImages[3].serviceStatus = ServiceStatus.ADMIN_DEL
        challengeTemplateImageRepository.saveAll(listOf(challengeTemplateImages[1], challengeTemplateImages[3]))

        // when
        val result = challengeTemplateImageRepository.findAllImageUrl(now)

        // then
        assertThat(result).containsExactly(challengeTemplateImages[2].imageUrl, challengeTemplateImages[4].imageUrl)
    }

    @DisplayName("챌린지 해시태그를 통한 검색 테스트")
    @Test
    fun searchChallengeByHashtag() {
        //given

        //when

        //then
    }

    private fun createAndSaveChallengeTemplateImage(
        admin: User,
        imageUrl: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        sort: Int,
    ): ChallengeTemplateImage {
        return challengeTemplateImageRepository.save(
            ChallengeTemplateImage(
                admin = admin,
                imageUrl = imageUrl,
                startDateTime = startDateTime,
                endDateTime = endDateTime
            )
        )
    }

    private fun createAndSaveAdminWithContact(): UserRole {
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

        return userRoleRepository.save(UserRole(user = user, role = Role.ADMIN))
    }
}