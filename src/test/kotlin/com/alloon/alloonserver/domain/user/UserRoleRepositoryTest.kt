package com.alloon.alloonserver.domain.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class UserRoleRepositoryTest {

    @Autowired
    private lateinit var userRoleRepository: UserRoleRepository
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var contactRepository: ContactRepository

    @DisplayName("회원 식별자로 모든 회원 권한을 조회하면 정상 작동한다")
    @Test
    fun givenValid_whenFindAllFetchUser_thenReturn() {
        // given
        val contact = createAndSaveContact()
        val user = createAndSaveUser(contact)
        val role = Role.USER
        val userRole = createAndSaveUserRole(user, role)

        // when
        val foundUserRoles = userRoleRepository.findAllFetchUser(user.id!!)

        // then
        assertAll(
            {
                assertThat(foundUserRoles)
                    .extracting("id", "role", "createDateTime", "updateDateTime", "user")
                    .containsExactly(
                        tuple(
                            userRole.id, userRole.role, userRole.createDateTime, userRole.updateDateTime,
                            userRole.user
                        )
                    )
            },
            {
                assertThat(foundUserRoles)
                    .extracting("user")
                    .extracting(
                        "id", "username", "password", "imageUrl", "temporaryPasswordYn", "createDateTime",
                        "updateDateTime", "contact"
                    )
                    .containsExactly(
                        tuple(
                            user.id, user.username, user.password, user.imageUrl, user.temporaryPasswordYn,
                            user.createDateTime, user.updateDateTime, user.contact
                        )
                    )
            }
        )
    }

    private fun createAndSaveUserRole(user: User, role: Role): UserRole {
        return userRoleRepository.save(UserRole(user = user, role = role))
    }

    private fun createAndSaveUser(contact: Contact): User {
        return userRepository.save(User(contact = contact, username = "tester", password = "password1!", imageUrl = ""))
    }

    private fun createAndSaveContact(): Contact {
        return contactRepository.save(
            Contact(
                email = "tester@alloon.com",
                verificationCode = "000000",
                verifyYn = true
            )
        )
    }
}