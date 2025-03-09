package com.photi.server.service.s3

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.photi.server.common.constant.ExceptionCode.SERVER_ERROR
import com.photi.server.common.response.CustomException
import com.photi.server.common.util.createFileName
import com.photi.server.common.util.validateFile
import com.photi.server.service.s3.FolderType.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URLDecoder
import kotlin.text.Charsets.UTF_8

@Service
class S3Service(private val amazonS3Client: AmazonS3Client) {

    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucket: String

    @Value("\${cloud.aws.s3.folder.folderName1}")
    private lateinit var userFolder: String

    @Value("\${cloud.aws.s3.folder.folderName2}")
    private lateinit var challengeFolder: String

    @Value("\${cloud.aws.s3.folder.folderName3}")
    private lateinit var feedFolder: String

    fun uploadImage(
        file: MultipartFile,
        folderType: FolderType,
        subFolder: Long? = null
    ): String {
        file.validateFile()

        val fileName = getRoot(folderType, subFolder) + file.createFileName()

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

    fun deleteImage(imageUrl: String, folderType: FolderType, subFolder: Long? = null) {
        try {
            val root = getRoot(folderType, subFolder)
            val fileName = imageUrl.substringAfter(root)
            val decodedFileName = URLDecoder.decode(fileName, UTF_8.toString())
            amazonS3Client.deleteObject(bucket, root + decodedFileName)
        } catch (e: IOException) {
            throw CustomException(SERVER_ERROR, e)
        }
    }

    private fun getRoot(folderType: FolderType, subFolder: Long? = null): String {
        return when (folderType) {
            USERS -> userFolder
            CHALLENGES -> challengeFolder
            CHALLENGE_EXAMPLES -> challengeFolder + "examples"
            FEEDS -> "$feedFolder$subFolder/"
        }
    }
}