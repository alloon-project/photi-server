package com.alloon.alloonserver.common.util

import com.photi.server.common.constant.ExceptionCode.IMAGE_TYPE_UNSUPPORTED
import com.photi.server.common.response.CustomException
import com.photi.server.common.util.validateFile
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.util.stream.Stream

class FileUtilityTest {

    @ParameterizedTest(name = "[{index}] {1}로 이미지 파일 타입 검증을 하면 정상 작동한다")
    @MethodSource("providerValidateImageFile")
    @DisplayName("이미지 파일 타입 검증을 하면 정상 작동한다")
    fun givenValid_whenValidateImageFile_thenReturn(file: MultipartFile) {
        // when & then
        assertDoesNotThrow { file.validateFile() }
    }

    @DisplayName("잘못된 파일 타입으로 이미지 파일 타입 검증을 하면 예외가 발생한다")
    @Test
    fun givenInvalidType_whenValidateImageFileType_thenThrow() {
        // given
        val file = MockMultipartFile("file", "file.txt", "text/plain", ByteArray(1))

        // when & then
        assertThatThrownBy { file.validateFile() }
            .isInstanceOf(CustomException::class.java)
            .extracting("exceptionCode")
            .isEqualTo(IMAGE_TYPE_UNSUPPORTED)
    }

    companion object {
        @JvmStatic
        fun providerValidateImageFile(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(MockMultipartFile("file", "file.png", "image/png", ByteArray(1))),
                Arguments.of(MockMultipartFile("file", "file.jpg", "image/jpg", ByteArray(1))),
                Arguments.of(MockMultipartFile("file", "file.jpeg", "image/jpeg", ByteArray(1))),
            )
        }
    }
}