package com.alloon.alloonserver.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserTemplateImageRepository : JpaRepository<UserTemplateImage, Long> {
}