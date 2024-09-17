package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.s3.S3Service
import com.alloon.alloonserver.service.user.dto.FindUserInfoDto
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("test")
@Transactional
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val s3Service = mockk<S3Service>()

    private val userService = UserService(userRepository, s3Service)

    @DisplayName("사용자 정보 조회를 하면 일치하는 사용자 정보를 반환한다.")
    @Test
    fun givenValid_whenFindUserInfo_thenReturn() {
        // given
        val userId = 1L
        val dto = FindUserInfoDto("https://url.kr/5MhHhD", "tester", "tester@photi.com")

        every { userRepository.findInfoById(any()) } returns dto

        // when
        val result = userService.findUserInfo(userId)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("존재하지 않은 사용자를 찾으려고 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenFindUserInfo_thenThrow() {
        // given
        val userId = 1L

        every { userRepository.findInfoById(any()) } returns null

        // when & then
        assertThatThrownBy { userService.findUserInfo(userId) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

//    @DisplayName("회원 이미지 업로드가 정상 작동한다")
//    @Test
//    fun givenValid_whenUploadImage_thenReturn() {
//        // given
//        val contact = createAndSaveContact()
//        val user = createAndSaveUser(contact)
//        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
//
//        // when
//        val response = userService.uploadImage(user.id!!, file)
//
//        // then
//        assertThat(response)
//            .extracting("userId", "username", "imageUrl", "email")
//            .containsExactly(user.id, user.username, user.imageUrl, contact.email)
//    }
//
//    @DisplayName("존재하지 않은 회원 식별자로 회원 이미지 업로드를 하면 예외가 발생한다")
//    @Test
//    fun givenNonExistingUserId_whenUploadImage_thenThrow() {
//        // given
//        val file = MockMultipartFile("file", "file.png", "image/png", ByteArray(1))
//
//        // when & then
//        assertThatThrownBy { userService.uploadImage(1L, file) }
//            .isInstanceOf(CustomException::class.java)
//            .extracting("exceptionCode")
//            .isEqualTo(USER_NOT_FOUND)
//    }
}