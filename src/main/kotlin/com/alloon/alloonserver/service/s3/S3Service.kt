package com.alloon.alloonserver.service.s3

import com.alloon.alloonserver.common.constant.ExceptionCode.SERVER_ERROR
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.FileUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class S3Service(
    private val amazonS3Client: AmazonS3Client,
) {
    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucket: String

    /**
     * S3에 이미지 업로드 후 이미지 URL을 반환한다.
     *
     * @param file 업로드할 이미지 파일
     * @param pathName 파일이 저장될 경로
     * @param fileName 저장될 파일 이름
     * @return 업로드된 이미지 URL
     * @throws CustomException 서버 오류가 났을 때 발생한다 ([SERVER_ERROR] 500)
     */
    fun uploadFile(file: MultipartFile?, pathName: String, fileName: String): String {
        FileUtility.validateImageFileType(file)

        val inputStream = file!!.inputStream
        val objectMetadata = ObjectMetadata().apply {
            contentLength = file.size
            contentType = file.contentType
        }

        return try {
            amazonS3Client.putObject(
                PutObjectRequest(bucket, "$pathName/$fileName", inputStream, objectMetadata)
                    .withCannedAcl(PublicRead)
            )
            amazonS3Client.getUrl(bucket, "$pathName/$fileName").toExternalForm()
        } catch (e: IOException) {
            throw CustomException(SERVER_ERROR, e.cause)
        }
    }
}