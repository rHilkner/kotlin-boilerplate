package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ServiceContext
import com.example.apiboilerplate.models.SysCallLog
import com.example.apiboilerplate.repositories.SysCallLogRepository
import org.springframework.stereotype.Service

@Service
class CallLogService(private val sysCallLogRepository: SysCallLogRepository) {

    fun saveContextToSysCallLog(ctx: ServiceContext) {
        ctx.sysCallLog = sysCallLogRepository.save(ctx.sysCallLog)
    }

}