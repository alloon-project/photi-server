package com.alloon.alloonserver.common.log

import com.alloon.alloonserver.common.response.CustomException
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component

@Aspect
@Component
class AspectLogging {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Pointcut("execution(public * com.alloon.alloonserver.domain..*(..))" +
            "|| execution(public * com.alloon.alloonserver.api..*(..))")
    private fun global() {}

    @Before("global()")
    fun beforeGlobal(jp: JoinPoint) {
        val signature = jp.signature as MethodSignature
        val className = signature.declaringTypeName.substringAfterLast(".")
        val method = signature.method
        val parameterNames = method.parameters
        val arguments = jp.args
        val paramLength = minOf(parameterNames.size, arguments.size)
        val uuid = InterceptorLogging().requestId

        val param = StringBuilder()
        for (i in 0 until paramLength) {
            param.append(parameterNames[i].name).append("=")
            param.append(arguments[i] ?: "null")

            if (i != parameterNames.size - 1) param.append(", ")
        }

        log.info("[{} | BEFORE] {} | {} ({})", uuid, className, method.name, param)
    }

    @AfterReturning(value = "global()", returning = "result")
    fun afterReturningGlobal(jp: JoinPoint, result: Any?) {
        val signature = jp.signature as MethodSignature
        val className = signature.declaringTypeName.substringAfterLast(".")
        val methodName = signature.method.name
        val uuid = InterceptorLogging().requestId
        var res = result.toString()

        if (methodName.contains("resultMasterPasswordScheduler"))
            res = res.replace(Regex("(?<=password\\s?=\\s?)\\S+"), "******")
                .replace(Regex("(?<=passwordReEnter\\s?=\\s?)\\S+"), "******")

        log.info("[{} | AFTER] {} | {} | return={}", uuid, className, methodName, res)
    }

    @AfterThrowing(value = "global()", throwing = "ex")
    fun afterThrowingGlobal(jp: JoinPoint, ex: CustomException) {
        val signature = jp.signature as MethodSignature
        val className = signature.declaringType.simpleName.substringAfterLast(".")
        val methodName = signature.method.name
        val errorName = ex.exceptionCode.name
        val uuid = InterceptorLogging().requestId

        log.error("========== [{} | ERROR] {} | {} | code={} ==========", uuid, className, methodName, errorName)

        if (ex.exceptionCode.httpStatus == INTERNAL_SERVER_ERROR)
            if (ex.throwable != null)
                log.error("========== [{} | DESCRIPTION] ==========", uuid, ex.throwable)
    }
}