package com.photi.server.common.util

import com.photi.server.common.constant.ExceptionCode.EMPTY_FILE_INVALID
import com.photi.server.common.constant.ExceptionCode.IMAGE_TYPE_UNSUPPORTED
import com.photi.server.common.constant.FileConstants.IMAGE_FILE_TYPE
import com.photi.server.common.response.CustomException
import org.springframework.web.multipart.MultipartFile
import java.util.*

fun MultipartFile.validateFile() {
    if (this.isEmpty) {
        throw CustomException(EMPTY_FILE_INVALID)
    }

    val fileType = this.contentType?.split("/")?.getOrNull(1)
    if (fileType !in IMAGE_FILE_TYPE.fields) {
        throw CustomException(IMAGE_TYPE_UNSUPPORTED)
    }
}

fun MultipartFile.createFileName(): String {
    return UUID.randomUUID().toString() + "_" + this.originalFilename
}