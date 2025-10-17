package com.photi.core.infra.s3

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.Headers
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.photi.core.domain.challenge.port.ChallengeS3Port
import com.photi.core.domain.common.consts.DirectoryType
import com.photi.core.domain.feed.port.FeedS3Port
import com.photi.core.domain.user.port.UserS3Port
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class S3Adapter(
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    private val s3Client: AmazonS3Client,
) : UserS3Port, FeedS3Port, ChallengeS3Port {

    override fun getPreSignedUrl(imageName: String, directory: DirectoryType): String {
        val request = getGeneratePreSignedUrlRequest(getUniqueImageName(directory, imageName))
        return s3Client.generatePresignedUrl(request).toString()
    }

    @Async
    override fun deleteImage(imageUrl: String, directory: DirectoryType) {
        val fileName = imageUrl.substringAfterLast("/")
        val key = directory.value + URLDecoder.decode(fileName, StandardCharsets.UTF_8)
        s3Client.deleteObject(bucket, key)
    }

    private fun getUniqueImageName(directory: DirectoryType, imageName: String) =
        directory.value + UUID.randomUUID().toString() + UNDERSCORE + imageName

    private fun getGeneratePreSignedUrlRequest(imageName: String) =
        GeneratePresignedUrlRequest(bucket, imageName)
            .withMethod(HttpMethod.PUT)
            .withExpiration(getExpiration())
            .apply {
                addRequestParameter(
                    Headers.S3_CANNED_ACL,
                    CannedAccessControlList.PublicRead.toString(),
                )
            }

    private fun getExpiration() = Date().apply { time += 1000 * 60 * 3 }

    companion object {
        private const val UNDERSCORE = "_"
    }
}
