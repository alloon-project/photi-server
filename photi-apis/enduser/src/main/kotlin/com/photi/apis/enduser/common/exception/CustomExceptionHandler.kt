package com.photi.apis.enduser.common.exception

import com.photi.apis.enduser.common.exception.dto.ErrorResponse
import com.photi.core.domain.common.exception.GlobalErrorCode
import com.photi.core.domain.common.exception.PhotiException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(PhotiException::class)
    protected fun handleCustomException(
        ex: PhotiException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse.of(ex.errorCode)
        return ResponseEntity.status(ex.errorCode.status).body(response)
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
        val message = ex.bindingResult.fieldErrors.joinToString { it.defaultMessage.toString() }
        val response = ErrorResponse(status.toString().split(" ")[1], message)
        return ResponseEntity.status(BAD_REQUEST).body(response)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = ex.constraintViolations.joinToString { it.message }
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

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val response = ErrorResponse(
            GlobalErrorCode.DATE_FORMAT_INVALID.name,
            GlobalErrorCode.DATE_FORMAT_INVALID.message,
        )
        return ResponseEntity.status(BAD_REQUEST).body(response)
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(Exception::class)
    protected fun handleException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex.message)
        val response = ErrorResponse.of(GlobalErrorCode.SERVER_ERROR)
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response)
    }
}
