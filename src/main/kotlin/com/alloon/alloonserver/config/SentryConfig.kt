package com.alloon.alloonserver.config

import com.alloon.alloonserver.common.log.InterceptorLogging
import io.sentry.Hint
import io.sentry.SentryEvent
import io.sentry.SentryOptions
import io.sentry.protocol.SentryId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SentryConfig(
    @Value("\${api.name}")
    private val serverName: String,
) : SentryOptions.BeforeSendCallback {

    override fun execute(event: SentryEvent, hint: Hint): SentryEvent? {
        event.serverName = serverName
        event.eventId = SentryId(InterceptorLogging().requestId)
        return event
    }
}