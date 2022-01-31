package com.example.apiboilerplate.base

import com.example.apiboilerplate.base.filters.request_context.AppHttpRequestWrapper
import com.example.apiboilerplate.base.filters.request_context.AppHttpResponseWrapper
import com.example.apiboilerplate.exceptions.ApiException
import com.example.apiboilerplate.models.SysCallLog
import org.slf4j.MDC
import java.util.*

/**
 * Service Context is generated at service execution start and passed on thread local as a persistent data share
 * across business method calls.
 *
 * contains diagnostic information about the call, most importantly a unique transaction ID which can be user to
 * track this service invocation.
 *
 */
class ServiceContext(request: AppHttpRequestWrapper, response: AppHttpResponseWrapper) {

    companion object {
        private var EXECUTION_ID_KEY = "executionId"
        private val threadLocal = ThreadLocal<ServiceContext>()

        @Synchronized
        fun getCurrentContext(): ServiceContext? {
            return threadLocal.get()
        }
    }

    // Object to SYS_CALL_LOG table
    var sysCallLog: SysCallLog? = null

    // Setting request context unique execution id
    var executionId: String = UUID.randomUUID().toString()

    // Incoming request
    var request: AppHttpRequestWrapper
    var response: AppHttpResponseWrapper
    var startDt: Date = Date()
    var endDt: Date? = null

    // Outgoing requests
    // TODO

    // Exception
    var apiException: ApiException? = null

    init {
        MDC.remove(EXECUTION_ID_KEY)
        MDC.put(EXECUTION_ID_KEY, this.executionId)
        threadLocal.set(this)
        this.request = request
        this.response = response
    }

}