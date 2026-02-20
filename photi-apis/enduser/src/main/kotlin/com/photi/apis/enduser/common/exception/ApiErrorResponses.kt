package com.photi.apis.enduser.common.exception

import com.photi.core.domain.appversion.exception.AppVersionErrorCode
import com.photi.core.domain.challenge.exception.ChallengeErrorCode
import com.photi.core.domain.challengemember.exception.ChallengeMemberErrorCode
import com.photi.core.domain.common.exception.GlobalErrorCode
import com.photi.core.domain.feed.exception.FeedErrorCode
import com.photi.core.domain.feedcomment.exception.FeedCommentErrorCode
import com.photi.core.domain.feedlike.exception.FeedLikeErrorCode
import com.photi.core.domain.user.exception.UserErrorCode
import com.photi.core.domain.userchallengehistory.exception.UserChallengeHistoryErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GlobalApiErrorResponses(val errorCodes: Array<GlobalErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AppVersionApiErrorResponses(val errorCodes: Array<AppVersionErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserApiErrorResponses(val errorCodes: Array<UserErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserChallengeHistoryApiErrorResponses(val errorCodes: Array<UserChallengeHistoryErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChallengeApiErrorResponses(val errorCodes: Array<ChallengeErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ChallengeMemberApiErrorResponses(val errorCodes: Array<ChallengeMemberErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class FeedApiErrorResponses(val errorCodes: Array<FeedErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class FeedCommentApiErrorResponses(val errorCodes: Array<FeedCommentErrorCode>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class FeedLikeApiErrorResponses(val errorCodes: Array<FeedLikeErrorCode>)
