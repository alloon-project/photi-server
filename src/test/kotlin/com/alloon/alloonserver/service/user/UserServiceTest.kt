package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.PasswordUtility
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.ContactRepository
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectResult
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.net.URL

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class UserServiceTest(
    @Autowired private val userService: UserService,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val contactRepository: ContactRepository,
    @Autowired private val passwordUtility: PasswordUtility,
) {

    @MockBean
    private lateinit var amazonS3Client: AmazonS3Client

    @BeforeEach
    fun beforeEach() {
        `when`(amazonS3Client.putObject(any()))
            .thenReturn(PutObjectResult())
        `when`(amazonS3Client.getUrl(any(), any()))
            .thenReturn(URL("https://localhost:8080/api/image/givenValid_whenUploadImage_thenReturn"))
    }

    @DisplayName("회원 정보를 조회가 정상 작동한다")
    @Test
    fun givenValid_whenGetInfo_thenReturn() {
        // given
        val contact = createAndSaveContact()
        val user = createAndSaveUser(contact)

        // when
        val response = userService.getInfo(user.id!!)

        // then
        assertThat(response)
            .extracting("userId", "username", "imageUrl", "email")
            .containsExactly(user.id!!, user.username, user.imageUrl, contact.email)
    }

    @DisplayName("존재하지 않은 회원 식별자로 회원 정보를 조회하면 예외가 발생한다")
    @Test
    fun givenNonExistingUserId_whenGetInfo_thenThrow() {
        // given
        val userId = 1L

        // when & then
        assertThatThrownBy { userService.getInfo(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    @DisplayName("회원 이미지 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadImage_thenReturn() {
        // given
        val contact = createAndSaveContact()
        val user = createAndSaveUser(contact)
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        // when
        val response = userService.uploadImage(user.id!!, file)

        // then
        assertThat(response)
            .extracting("userId", "username", "imageUrl", "email")
            .containsExactly(user.id, user.username, user.imageUrl, contact.email)
    }

    @DisplayName("존재하지 않은 회원 식별자로 회원 이미지 업로드를 하면 예외가 발생한다")
    @Test
    fun givenNonExistingUserId_whenUploadImage_thenThrow() {
        // given
        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))

        // when & then
        assertThatThrownBy { userService.uploadImage(1L, file) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    private fun createAndSaveContact(): Contact {
        val contact = Contact(email = "tester@alloon.com", verificationCode = "000000", verifyYn = true)
        return contactRepository.save(contact)
    }

    private fun createAndSaveUser(contact: Contact): User {
        val encryptedPassword = passwordUtility.encryptPassword("password1!")
        val user = User(contact = contact, username = "tester", password = encryptedPassword, imageUrl = "")
        return userRepository.save(user)
    }
}