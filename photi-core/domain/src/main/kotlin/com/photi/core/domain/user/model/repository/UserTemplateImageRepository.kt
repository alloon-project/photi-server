package com.photi.core.domain.user.model.repository

import com.photi.core.domain.user.model.UserTemplateImage
import org.springframework.data.jpa.repository.JpaRepository

interface UserTemplateImageRepository : JpaRepository<UserTemplateImage, Long>
