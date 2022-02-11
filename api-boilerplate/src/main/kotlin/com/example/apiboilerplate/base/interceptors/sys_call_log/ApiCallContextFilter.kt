package com.example.apiboilerplate.base.interceptors.sys_call_log

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.base.logger.LoggerDelegate
import com.example.apiboilerplate.services.base.SysCallLogService
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ApiCallContextFilter(private val sysCallLogService: SysCallLogService) : Filter {

    companion object { private val log by LoggerDelegate() }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        if (response.characterEncoding == null) {
            response.characterEncoding = "UTF-8"; // Or whatever default. UTF-8 is good for World Domination.
        }

        // Instantiate wrappers for request and response objects to be able to read data
        val requestWrapper = AppHttpRequestWrapper(request as HttpServletRequest)
        val responseWrapper = AppHttpResponseWrapper(response as HttpServletResponse)
        val apiCallContext = ApiCallContext(requestWrapper, responseWrapper)
        log.info("Start api-call-context with executionId [{}]", apiCallContext.executionId)
        sysCallLogService.saveContextToSysCallLog(apiCallContext)

        try {
            // Execute request
            chain.doFilter(requestWrapper, responseWrapper)
            responseWrapper.flushBuffer()
        } finally {
            // Save api-session to database and clear context
            apiCallContext.endDt = Date()
            val executionId = apiCallContext.executionId
            sysCallLogService.saveContextToSysCallLog(apiCallContext)
            ApiCallContext.clearApiCallContext()
            log.info("Finish api-call-context with executionId [{}]", executionId)
        }

    }

}
