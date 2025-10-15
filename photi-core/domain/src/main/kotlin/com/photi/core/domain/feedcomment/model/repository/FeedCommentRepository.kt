package com.photi.core.domain.feedcomment.model.repository

import com.photi.core.domain.feedcomment.model.FeedComment
import org.springframework.data.jpa.repository.JpaRepository

interface FeedCommentRepository : JpaRepository<FeedComment, Long>, FeedCommentCustomRepository
