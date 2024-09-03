package com.alloon.alloonserver.common.exception

import com.alloon.alloonserver.common.constant.ExceptionCode.FILE_SIZE_EXCEED
import com.alloon.alloonserver.common.constant.ExceptionCode.SERVER_ERROR
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.response.ErrorResponse
import io.sentry.Sentry
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.*
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(CustomException::class)
    protected fun handleCustomException(
        ex: CustomException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.of(ex.exceptionCode)
        return ResponseEntity.status(ex.exceptionCode.httpStatus).body(response)
    }

    @ExceptionHandler(RuntimeException::class)
    protected fun handleUndefinedException(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.info("Exception : ${ex.message}")
        val response = ErrorResponse.of(SERVER_ERROR)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response)
    }

    /**
     * 400 Bad Request
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val message = ex.bindingResult.fieldErrors.map {
            mapOf(it.field to it.defaultMessage)
        }
        val response = ErrorResponse(status.toString().split(" ")[1], message)

        return ResponseEntity.status(BAD_REQUEST).body(response)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = mutableListOf<Map<String?, String>>()
        ex.constraintViolations.forEach {
            val propertyPaths = it.propertyPath.toString().split(".")
            val property = propertyPaths.lastOrNull()
            message.add(mapOf(property to it.message))
        }
        val response = ErrorResponse(BAD_REQUEST.name, message)

        return ResponseEntity.status(BAD_REQUEST).body(response)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val message = ex.mostSpecificCause.message.toString()
        val response = ErrorResponse(status.toString().split(" ")[1], message)

        return ResponseEntity.status(BAD_REQUEST).body(response)
    }

    /**
     * 413 Payload Too Large
     */
    override fun handleMaxUploadSizeExceededException(
        ex: MaxUploadSizeExceededException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val response = ErrorResponse.of(FILE_SIZE_EXCEED)
        return ResponseEntity.status(PAYLOAD_TOO_LARGE).body(response)
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(Exception::class)
    protected fun handleException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        Sentry.captureException(ex)
        val response = ErrorResponse.of(SERVER_ERROR)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response)
    }
}