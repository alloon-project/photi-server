package com.alloon.alloonserver.common.exception

import com.alloon.alloonserver.common.constant.ExceptionCode.*
import com.alloon.alloonserver.common.response.CustomException
import com.alloon.alloonserver.common.response.ErrorResponse
import com.alloon.alloonserver.common.response.ExceptionResponse
import io.sentry.Sentry
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.*
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(CustomException::class)
    protected fun handleCustomException(ex: CustomException): ResponseEntity<ExceptionResponse> {
        return ExceptionResponse.toResponseEntity(ex)
    }

    @ExceptionHandler(RuntimeException::class)
    protected fun handleUndefinedException(ex: RuntimeException): ResponseEntity<ExceptionResponse> {
        logger.info("Exception : ${ex.message}")
        return ExceptionResponse.toResponseEntity(CustomException(SERVER_ERROR, ex.cause))
    }

    override fun handleServletRequestBindingException(
        ex: ServletRequestBindingException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("${ex.message}")
        return super.handleServletRequestBindingException(ex, headers, status, request)
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
        val httpServletRequest = (request as ServletWebRequest).request
        val path = httpServletRequest.requestURL.toString()

        val response = ErrorResponse(status.value(), status.toString().split(" ")[1], message, path)

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
        val path = request.requestURL.toString()

        val response = ErrorResponse(BAD_REQUEST.value(), BAD_REQUEST.name, message, path)

        return ResponseEntity.status(BAD_REQUEST).body(response)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val message = ex.mostSpecificCause.message.toString()
        val httpServletRequest = (request as ServletWebRequest).request
        val path = httpServletRequest.requestURL.toString()

        val response = ErrorResponse(status.value(), status.toString().split(" ")[1], message, path)

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
        val httpServletRequest = (request as ServletWebRequest).request
        val path = httpServletRequest.requestURL.toString()
        val response = ErrorResponse.of(FILE_SIZE_EXCEED, path)
        return ResponseEntity.status(PAYLOAD_TOO_LARGE).body(response)
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(Exception::class)
    protected fun handleException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        Sentry.captureException(ex)
        val path = request.requestURL.toString()
        val response = ErrorResponse.of(SERVER_ERROR, path)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response)
    }
}