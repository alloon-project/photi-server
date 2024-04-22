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

    /**
     * 회원 정보 조회
     * @param userId 회원 식별자
     * @throws USER_NOT_FOUND 404
     * @return 회원 정보 조회 응답
     */
    fun getInfo(userId: Long): UserGetInfoResponse {
        val user = userRepository.findFetchContact(null, null, userId)
            ?: throw CustomException(USER_NOT_FOUND)

        return UserGetInfoResponse(user)
    }

    /**
     * 회원 이미지 업로드
     * @param userId 회원 식별자
     * @param file 파일
     * @throws FILE_FIELD_REQUIRED 400
     * @throws USER_NOT_FOUND 404
     * @throws IMAGE_TYPE_UNSUPPORTED 415
     * @throws SERVER_ERROR 500
     * @return 회원 이미지 업로드 응답
     */
    @Transactional
    fun uploadImage(userId: Long, file: MultipartFile?): UserUploadImageResponse {
        val user = userRepository.find(userId) ?: throw CustomException(USER_NOT_FOUND)

        val imageUrl = s3Service.uploadFile(file, "users/$userId", UUID.randomUUID().toString())
        user.changeImageUrl(imageUrl)

        return UserUploadImageResponse(user)
    }
}