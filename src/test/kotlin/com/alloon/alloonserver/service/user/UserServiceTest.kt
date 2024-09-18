package com.alloon.alloonserver.service.user

import com.alloon.alloonserver.common.constant.ExceptionCode.USER_NOT_FOUND
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.Contact
import com.alloon.alloonserver.domain.user.User
import com.alloon.alloonserver.domain.user.UserRepository
import com.alloon.alloonserver.framework.TestContainerInitializer
import com.alloon.alloonserver.service.s3.S3Service
import com.alloon.alloonserver.service.user.dto.FindUserInfoDto
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
        val dto = getUserInfoDto()

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

    @DisplayName("사용자 프로필 이미지 업로드를 하면 일치하는 사용자 정보를 반환한다.")
    @Test
    fun givenValid_whenUpdateUserImage_thenReturn() {
        // given
        val userId = 1L
        val multipartFile = getMultipartFile()
        val user = getUser()
        val imageUrl = "https://url.kr/5MhHhD"
        val dto = getUserInfoDto()

        every { userRepository.findById(any()) } returns Optional.of(user)
        every { s3Service.deleteImage(any(), any()) } just Runs
        every { s3Service.uploadImage(any(), any()) } returns ""
        every { s3Service.getImageUrl(any()) } returns imageUrl

        // when
        val result = userService.updateUserImage(userId, multipartFile)

        // then
        assertThat(result).isEqualTo(dto)
    }

    @DisplayName("존재하지 않은 사용자로 이미지 업로드를 하면 예외가 발생한다.")
    @Test
    fun givenNotFoundUser_whenUpdateUserImage_thenThrow() {
        // given
        val userId = 1L
        val multipartFile = getMultipartFile()

        every { userRepository.findById(any()) } returns Optional.empty()

        // when & then
        assertThatThrownBy { userService.updateUserImage(userId, multipartFile) }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(USER_NOT_FOUND)
    }

    private fun getUser(): User {
        val contact = Contact(1L, "tester@photi.com", "000000", true)
        return User(1L, contact, "tester", "password1!", "")
    }

    private fun getUserInfoDto(): FindUserInfoDto {
        return FindUserInfoDto("https://url.kr/5MhHhD", "tester", "tester@photi.com")
    }

    private fun getMultipartFile(): MockMultipartFile {
        return MockMultipartFile("imageFile", "file.png", "image/png", ByteArray(1))
    }
}