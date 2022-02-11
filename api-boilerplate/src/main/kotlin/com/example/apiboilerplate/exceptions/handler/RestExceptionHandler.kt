package com.example.apiboilerplate.exceptions.handler

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.dtos.ApiErrorDTO
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

    @ExceptionHandler(Exception::class)
    protected fun handleUnexpectedError(ex: Exception): ResponseEntity<ApiErrorDTO> {
        return handleException(ex)
    }

    /**
     * returns Response Entity for exception
     */
    private fun handleException(ex: Exception): ResponseEntity<ApiErrorDTO> {

        // Create ApiErrorDTO and response entity for exception
        val apiException = if (ex is ApiException) ex else ApiExceptionModule.General.UnexpectedException(ex)
        val apiErrorDTO = ApiErrorDTO(apiException)
        val responseEntity = ResponseEntity(apiErrorDTO, apiException.httpStatus)

        // Set context variables and save context to database table SYS_ERROR_LOG
        ApiCallContext.getCurrentApiCallContext().apiException = apiException
        errorLogService.saveApiExceptionToSysErrorLog(apiException)

        return responseEntity
    }

}