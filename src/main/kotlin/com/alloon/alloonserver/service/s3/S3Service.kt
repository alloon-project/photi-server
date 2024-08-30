package com.alloon.alloonserver.service.s3

import com.alloon.alloonserver.common.constant.ExceptionCode.SERVER_ERROR
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.util.createFileName
import com.alloon.alloonserver.common.util.validateFile
import com.alloon.alloonserver.service.s3.FolderType.*
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class S3Service(private val amazonS3Client: AmazonS3Client) {

    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucket: String

    @Value("\${cloud.aws.s3.folder.folderName1}")
    private lateinit var userFolder: String

    @Value("\${cloud.aws.s3.folder.folderName2}")
    private lateinit var challengeFolder: String

    fun uploadImage(file: MultipartFile, folderType: FolderType): String {
        file.validateFile()

        val fileName = getRoot(folderType) + file.createFileName()

        val objectMetadata = ObjectMetadata().apply {
            contentLength = file.size
            contentType = file.contentType
        }

        return try {
            amazonS3Client.putObject(
                PutObjectRequest(bucket, fileName, file.inputStream, objectMetadata)
                    .withCannedAcl(PublicRead)
            )
            fileName
        } catch (e: IOException) {
            throw CustomException(SERVER_ERROR, e)
        }
    }

    fun getImageUrl(fileName: String): String {
        return amazonS3Client.getUrl(bucket, fileName).toString()
    }

    fun getChallengeExampleImages(): List<String> {
        return amazonS3Client.listObjects(bucket, getRoot(CHALLENGE_EXAMPLES))
            .objectSummaries
            .map { it.key }
            .filter { it.endsWith(".jpg") }
            .map { getImageUrl(it) }
    }

    private fun getRoot(folderType: FolderType): String {
        return when (folderType) {
            USERS -> userFolder
            CHALLENGES -> challengeFolder
            CHALLENGE_EXAMPLES -> challengeFolder + "examples"
        }
    }
}