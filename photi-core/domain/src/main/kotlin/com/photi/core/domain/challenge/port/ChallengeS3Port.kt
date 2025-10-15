package com.photi.core.domain.challenge.port

import com.photi.core.domain.common.consts.DirectoryType

interface ChallengeS3Port {

    fun getPreSignedUrl(imageName: String, directory: DirectoryType): String

    fun deleteImage(imageUrl: String, directory: DirectoryType)
}
