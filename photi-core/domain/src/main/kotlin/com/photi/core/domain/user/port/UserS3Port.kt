package com.photi.core.domain.user.port

import com.photi.core.domain.common.consts.DirectoryType

interface UserS3Port {

    fun getPreSignedUrl(imageName: String, directory: DirectoryType): String

    fun deleteImage(imageUrl: String, directory: DirectoryType)
}
