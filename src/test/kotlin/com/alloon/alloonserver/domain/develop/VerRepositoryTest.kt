package com.alloon.alloonserver.domain.develop

import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VerRepositoryTest(
    @Autowired private val verRepository: VerRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userRoleRepository: UserRoleRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @ParameterizedTest(name = "[{index}] 현재 버전이 {0}이고 가능 버전이 {1}일때 강제 업데이트 필요 여부 조회를 하면 {2}를 반환한다")
    @MethodSource("providerExists")
    @DisplayName("강제 업데이트 필요 여부 조회를 하면 정상 작동한다")
    fun givenValid_whenExists_thenReturn(currentVersion: String, availableVersion: String, expected: Boolean) {
        // given
        val adminRole = createAndSaveAdminWithContact()

        createAndSaveAppVersion(availableVersion, adminRole.user)

        // when
        val result = verRepository.exists(currentVersion)

        // then
        assertThat(result).isEqualTo(expected)
    }

    private fun createAndSaveAppVersion(version: String, admin: User): Ver {
        return verRepository.save(Ver(version = version, admin = admin))
    }

    private fun createAndSaveAdminWithContact(): UserRole {
        val contact = contactRepository.save(Contact(
            email = "tester@alloon.com",
            verificationCode = "000000",
            verifyYn = true
        ))

        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = userRepository.save(User(contact = contact, username = "tester", password = encryptedPassword, imageUrl = ""))

        return userRoleRepository.save(UserRole(user = user, role = Role.ADMIN))
    }

    companion object {
        @JvmStatic
        fun providerExists(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("1.0.0", "1.1.0", true),
                Arguments.of("1.0.2", "1.0.0", false)
            )
        }
    }
}