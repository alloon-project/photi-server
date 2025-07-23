package com.photi.server.common.log

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.util.StopWatch
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.util.*

class InterceptorLogging : HandlerInterceptor {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    private val stopWatchThreadLocal: ThreadLocal<StopWatch> = ThreadLocal()
    private val uuidThreadLocal: ThreadLocal<String> = ThreadLocal()

    val requestId:String = uuidThreadLocal.toString().substringAfterLast("@")

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val uuid = UUID.randomUUID().toString()
        val method = request.method
        val uri = request.requestURI
        val stopWatch = StopWatch()

        stopWatch.start()

        log.info("========== [{} | START] {} {} ==========", uuid, method, uri)

        uuidThreadLocal.set(uuid)
        stopWatchThreadLocal.set(stopWatch)

        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        val stopWatch = stopWatchThreadLocal.get()

        stopWatch.stop()

        val time = stopWatch.totalTimeMillis
        val uuid = uuidThreadLocal.get()
        val method = request.method
        val uri = request.requestURI

        log.info("========== [{} | FINISH] {} {} | time={}ms ==========", uuid, method, uri, time)

        uuidThreadLocal.remove()
        stopWatchThreadLocal.remove()
    }
}