package com.alloon.alloonserver.common.util

import com.alloon.alloonserver.common.constant.ExceptionCode.FILE_FIELD_REQUIRED
import com.alloon.alloonserver.common.constant.ExceptionCode.IMAGE_TYPE_UNSUPPORTED
import com.alloon.alloonserver.common.constant.FileConstants.IMAGE_FILE_TYPE
import com.alloon.alloonserver.common.response.CustomException
import org.springframework.web.multipart.MultipartFile

class FileUtility {

    companion object {

        /**
         * 이미지 파일 타입의 유효성을 검증한다.
         *
         * @param file 이미지 파일
         * @throws CustomException 파일이 없을 때 발생한다 ([FILE_FIELD_REQUIRED] 400)
         * @throws CustomException 이미지 타입이 .jpeg, .jpg, .png가 아닐 때 발생한다 ([IMAGE_TYPE_UNSUPPORTED] 415)
         */
        fun validateImageFileType(file: MultipartFile?) {
            requireNotNull(file) { throw CustomException(FILE_FIELD_REQUIRED) }

            val contentType = file.contentType ?: ""
            val fileType = contentType.split("/").getOrNull(1) ?: ""

            if (fileType !in IMAGE_FILE_TYPE.fields) {
                throw CustomException(IMAGE_TYPE_UNSUPPORTED)
            }
        }
    }
}