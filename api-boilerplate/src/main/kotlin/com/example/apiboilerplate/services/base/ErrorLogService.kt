package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.exceptions.ApiException
import com.example.apiboilerplate.models.base.SysErrorLog
import com.example.apiboilerplate.repositories.SysErrorLogRepository
import org.springframework.stereotype.Service

@Service
class ErrorLogService(private val sysErrorLogRepository: SysErrorLogRepository) {

    fun saveApiExceptionToSysErrorLog(apiException: ApiException) {

        val sysErrorLog = SysErrorLog(
            ApiSessionContext.getCurrentApiCallContext().sysCallLog.callLogId,
            apiException.httpStatus.name,
            apiException.httpStatus.value().toString(),
            apiException.javaClass.name,
            apiException.stackTraceToString(),
            apiException.errorMessage,
            apiException.debugMessage,
            apiException.timestamp
        )

        sysErrorLogRepository.save(sysErrorLog)
    }

}