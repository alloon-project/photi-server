package com.photi.core.domain.s3

import com.photi.core.domain.common.consts.FileConstants
import com.photi.core.domain.common.exception.CustomException
import com.photi.core.domain.common.exception.ExceptionCode
import org.springframework.web.multipart.MultipartFile
import java.util.*

fun MultipartFile.validateFile() {
    if (this.isEmpty) {
        throw CustomException(ExceptionCode.EMPTY_FILE_INVALID)
    }

    val fileType = this.contentType?.split("/")?.getOrNull(1)
    if (fileType !in FileConstants.IMAGE_FILE_TYPE.fields) {
        throw CustomException(ExceptionCode.IMAGE_TYPE_UNSUPPORTED)
    }
}

fun MultipartFile.createFileName(): String {
    return UUID.randomUUID().toString() + "_" + this.originalFilename
}
