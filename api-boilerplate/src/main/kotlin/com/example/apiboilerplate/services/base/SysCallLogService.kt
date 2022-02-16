package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.repositories.SysCallLogRepository
import org.springframework.stereotype.Service

@Service
class SysCallLogService(private val sysCallLogRepository: SysCallLogRepository) {

    fun saveContextToSysCallLog(ctx: ApiSessionContext) {
        ctx.sysCallLog = sysCallLogRepository.save(ctx.sysCallLog)
    }

}