package com.photi.core.infra

import com.photi.core.infra.async.AsyncConfig
import com.photi.core.infra.jpa.JpaConfig
import com.photi.core.infra.querydsl.QuerydslConfig
import com.photi.core.infra.redis.RedisConfig
import com.photi.core.infra.s3.S3Config

enum class PhotiConfigGroup(
    val configClass: Class<out PhotiConfig>,
) {

    JPA(JpaConfig::class.java),
    QUERYDSL(QuerydslConfig::class.java),
    REDIS(RedisConfig::class.java),
    S3(S3Config::class.java),
    ASYNC(AsyncConfig::class.java),
}
