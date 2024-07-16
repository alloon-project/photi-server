package com.alloon.alloonserver.service.s3

import com.amazonaws.services.s3.AmazonS3Client
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.ReflectionTestUtils
import java.net.URL

@ActiveProfiles("test")
class S3ServiceTest {

    @Mock
    val amazonS3Client: AmazonS3Client = mock(AmazonS3Client::class.java)
    val s3Service: S3Service = S3Service(amazonS3Client)

    @BeforeEach
    fun beforeEach() {
        ReflectionTestUtils.setField(s3Service, "bucket", "sample")
        Mockito.`when`(amazonS3Client.getUrl(any(), any()))
            .thenReturn(URL("https://localhost:8080/s3/image/givenValid_whenUploadFile_thenReturn"))
    }

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
        Mockito.verify(amazonS3Client, times(1)).getUrl(any(), any())
        assertThat(imageUrl).isNotBlank()
    }
}