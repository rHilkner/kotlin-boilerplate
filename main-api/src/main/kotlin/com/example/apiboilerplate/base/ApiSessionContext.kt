package com.example.apiboilerplate.base

import com.example.apiboilerplate.base.interceptors.sys_call_log.AppHttpRequestWrapper
import com.example.apiboilerplate.base.interceptors.sys_call_log.AppHttpResponseWrapper
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiException
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.models.base.SysCallLog
import com.example.apiboilerplate.models.user.AppUser
import org.slf4j.MDC
import java.util.*

/**
 * API Call Context is generated for every incoming request and passed on thread local as a persistent data share
 * across business method calls. It contains diagnostic information about the call (request and response), most
 * importantly a unique transaction ID which can be user to track this service invocation.
 */
class ApiSessionContext(
    var request: AppHttpRequestWrapper,
    var response: AppHttpResponseWrapper
) {

    companion object {
        private var TRANSACTION_ID_KEY = "transactionId"
        private val threadLocal = ThreadLocal<ApiSessionContext>()

        @Synchronized
        fun getCurrentApiCallContext(): ApiSessionContext {
            val apiSessionContext = threadLocal.get()
            return apiSessionContext ?: throw ApiExceptionModule.General.NullPointer("API Session Context is null (?)")
        }

        fun clearApiCallContext() {
            threadLocal.remove()
            MDC.remove(TRANSACTION_ID_KEY)
        }
    }

    // Set request context unique transaction id
    var transactionId: String = UUID.randomUUID().toString()

    var startDt: Date = Date()
    var endDt: Date? = null

    // Exception
    var apiException: ApiException? = null

    // API session info
    var apiSession: ApiSession? = null
    val currentUserId: Long?
        get() = apiSession?.userId
    val currentUserRole: UserRole?
        get() = apiSession?.role
    var currentUser: AppUser? = null

    // Object to SYS_CALL_LOG table
    var sysCallLog: SysCallLog = SysCallLog(this)
        get() { // update before retrieving information
            field.updateData(this)
            return field
        }

    init {
        // Logging transaction-id to SLF4J logger
        MDC.remove(TRANSACTION_ID_KEY)
        MDC.put(TRANSACTION_ID_KEY, this.transactionId)
        // Create "global variable" for this object to consult information about the user and its permissions
        threadLocal.set(this)
    }

}