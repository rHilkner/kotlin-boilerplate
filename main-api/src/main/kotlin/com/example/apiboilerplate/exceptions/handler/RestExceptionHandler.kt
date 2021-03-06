package com.example.apiboilerplate.exceptions.handler

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.exception.ApiErrorDTO
import com.example.apiboilerplate.exceptions.ApiException
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.base.ErrorLogService
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler(private val errorLogService: ErrorLogService) : ResponseEntityExceptionHandler() {

    companion object { private val log by ApiLogger() }

    @ExceptionHandler(Exception::class)
    protected fun handleUnexpectedError(ex: Exception): ResponseEntity<ApiErrorDTO> {
        return handleException(ex)
    }

    /**
     * returns Response Entity for exception
     */
    private fun handleException(ex: Exception): ResponseEntity<ApiErrorDTO> {

        // Log error
        log.error("The following exception is being saved to database by RestExceptionHandler", ex)

        // Create ApiErrorDTO and response entity for exception
        val apiException = if (ex is ApiException) ex else ApiExceptionModule.General.UnexpectedException("Unexpected error", ex.stackTraceToString())
        val apiErrorDTO = ApiErrorDTO(apiException)
        val responseEntity = ResponseEntity(apiErrorDTO, apiException.httpStatus)

        // Set context variables and save context to database table SYS_ERROR_LOG
        ApiSessionContext.getCurrentApiCallContext().apiException = apiException
        errorLogService.saveApiExceptionToSysErrorLog(apiException)

        log.debug("Exception successfully saved to database, responding API")

        return responseEntity
    }

}