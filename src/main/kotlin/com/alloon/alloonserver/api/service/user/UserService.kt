package com.alloon.alloonserver.api.service.user

import com.alloon.alloonserver.api.service.s3.S3Service
import com.alloon.alloonserver.api.service.user.response.UserGetInfoResponse
import com.alloon.alloonserver.api.service.user.response.UserUploadImageResponse
import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Validated
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) {

    fun getInfo(userId: Long): UserGetInfoResponse {
        val user = userRepository.findFetchContact(null, null, userId)
            ?: throw CustomException(USER_NOT_FOUND)

        return UserGetInfoResponse(user)
    }

    @Transactional
    fun uploadImage(userId: Long, file: MultipartFile?): UserUploadImageResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val imageUrl = s3Service.uploadFile(file, "users/$userId", UUID.randomUUID().toString())
        user.changeImageUrl(imageUrl)

        return UserUploadImageResponse(user)
    }
}