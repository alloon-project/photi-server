package com.photi.core.domain.feed.port

import com.photi.core.domain.common.consts.DirectoryType

interface FeedS3Port {

    fun getPreSignedUrl(imageName: String, directory: DirectoryType): String

    fun deleteImage(imageUrl: String, directory: DirectoryType)
}
