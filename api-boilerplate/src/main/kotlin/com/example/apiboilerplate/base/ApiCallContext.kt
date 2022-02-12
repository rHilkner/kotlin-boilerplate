package com.example.apiboilerplate.base

import com.example.apiboilerplate.base.interceptors.sys_call_log.AppHttpRequestWrapper
import com.example.apiboilerplate.base.interceptors.sys_call_log.AppHttpResponseWrapper
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiException
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.models.base.SysCallLog
import org.slf4j.MDC
import java.util.*

/**
 * API Call Context is generated for every incoming request and passed on thread local as a persistent data share
 * across business method calls. It contains diagnostic information about the call (request and response), most
 * importantly a unique transaction ID which can be user to track this service invocation.
 */
class ApiCallContext(request: AppHttpRequestWrapper, response: AppHttpResponseWrapper) {

    companion object {
        private var EXECUTION_ID_KEY = "executionId"
        private val threadLocal = ThreadLocal<ApiCallContext>()

        @Synchronized
        fun getCurrentApiCallContext(): ApiCallContext {
            return threadLocal.get()
        }

        fun clearApiCallContext() {
            threadLocal.remove();
            MDC.remove(EXECUTION_ID_KEY)
        }
    }

    // Set request context unique execution id
    var executionId: String = UUID.randomUUID().toString()

    // Request/Response
    var request: AppHttpRequestWrapper
    var response: AppHttpResponseWrapper
    var startDt: Date = Date()
    var endDt: Date? = null

    // Exception
    var apiException: ApiException? = null

    // API call objects
    var apiSession: ApiSession? = null
    val currentUserId: Long?
        get() = apiSession?.userId
    val currentUserRole: UserRole?
        get() = apiSession?.role
    var currentUser: AppUser? = null

    // Object to SYS_CALL_LOG table
    var sysCallLog: SysCallLog = SysCallLog(this)
        get() {
            field.updateData(this)
            return field
        }

    init {
        MDC.remove(EXECUTION_ID_KEY)
        MDC.put(EXECUTION_ID_KEY, this.executionId)
        threadLocal.set(this)
        this.request = request
        this.response = response
    }

}