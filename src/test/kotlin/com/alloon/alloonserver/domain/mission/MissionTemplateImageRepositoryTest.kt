package com.alloon.alloonserver.domain.mission

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.base.ServiceStatus
import com.alloon.alloonserver.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MissionTemplateImageRepositoryTest(
    @Autowired private val missionTemplateImageRepository: MissionTemplateImageRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {
    @DisplayName("미션 예시 이미지 전체 조회가 정상 작동한다")
    @Test
    fun givenValid_whenFind_thenReturn() {
        // given
        val adminRole = createAndSaveAdminWithContact()

        val now = LocalDateTime.now()

        val missionTemplateImages: List<MissionTemplateImage> = listOf(
            createAndSaveMissionTemplateImage(adminRole.user, "image1", now.minusSeconds(2),
                now.minusSeconds(1), 1),
            createAndSaveMissionTemplateImage(adminRole.user, "image2", now.minusSeconds(2),
                now.plusSeconds(2), 2),
            createAndSaveMissionTemplateImage(adminRole.user, "image3", now.minusSeconds(2),
                now.plusSeconds(2), 3),
            createAndSaveMissionTemplateImage(adminRole.user, "image4", now.minusSeconds(1),
                now.plusSeconds(1), 4),
            createAndSaveMissionTemplateImage(adminRole.user, "image5", now.minusSeconds(1),
                now.plusSeconds(1), 5),
            createAndSaveMissionTemplateImage(adminRole.user, "image6", now.plusSeconds(1),
                now.plusSeconds(2), 6),
        )
        missionTemplateImages[1].serviceStatus = ServiceStatus.ADMIN_DEL
        missionTemplateImages[3].serviceStatus = ServiceStatus.ADMIN_DEL
        missionTemplateImageRepository.saveAll(listOf(missionTemplateImages[1], missionTemplateImages[3]))

        // when
        val result = missionTemplateImageRepository.findAllImageUrl(now)

        // then
        assertThat(result).containsExactly(missionTemplateImages[2].imageUrl, missionTemplateImages[4].imageUrl)
    }

    private fun createAndSaveMissionTemplateImage(
        admin: User,
        imageUrl: String,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime,
        sort: Int,
    ): MissionTemplateImage {
        return missionTemplateImageRepository.save(MissionTemplateImage(
            admin = admin,
            imageUrl = imageUrl,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            sort = sort,
        ))
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
        val user = userRepository.save(User(contact = contact, username = "tester", password = encryptedPassword, imageUrl = ""))

        return userRoleRepository.save(UserRole(user = user, role = Role.ADMIN))
    }
}