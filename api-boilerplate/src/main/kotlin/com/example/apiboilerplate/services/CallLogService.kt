package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ServiceContext
import com.example.apiboilerplate.models.SysCallLog
import com.example.apiboilerplate.repositories.SysCallLogRepository
import org.springframework.stereotype.Service

@Service
class CallLogService(private val sysCallLogRepository: SysCallLogRepository) {

    fun saveContextToSysCallLog(ctx: ServiceContext) {

        val sysCallLog: SysCallLog = if (ctx.sysCallLog == null) {
            SysCallLog(ctx)
        } else {
            ctx.sysCallLog!!.updateData(ctx)
            ctx.sysCallLog!!
        }

        ctx.sysCallLog = sysCallLogRepository.save(sysCallLog)
    }

}