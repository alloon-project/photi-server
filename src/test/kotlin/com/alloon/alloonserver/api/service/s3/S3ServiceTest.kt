package com.alloon.alloonserver.api.service.s3

import com.amazonaws.services.s3.AmazonS3Client
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class S3ServiceTest(
    @Autowired private val s3Service: S3Service,
) {

    @DisplayName("파일 업로드가 정상 작동한다")
    @Test
    fun givenValid_whenUploadFile_thenReturn() {
        // given
        val file = MockMultipartFile("file", "filename.jpg", "image/jpeg", ByteArray(1))
        val pathName = "test"
        val fileName = "file"

        // when
        val imageUrl = s3Service.uploadFile(file, pathName, fileName)

        // then
        assertThat(imageUrl).isNotBlank()
    }
}