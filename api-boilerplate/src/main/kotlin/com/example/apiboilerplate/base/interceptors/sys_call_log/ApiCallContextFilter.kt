package com.example.apiboilerplate.base.interceptors.sys_call_log

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.services.base.SysCallLogService
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ApiCallContextFilter(private val sysCallLogService: SysCallLogService) : Filter {

    companion object { private val log by ApiLogger() }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        if (response.characterEncoding == null) {
            response.characterEncoding = "UTF-8"; // Or whatever default. UTF-8 is good for World Domination.
        }

        // Instantiate wrappers for request and response objects to be able to read data
        val requestWrapper = AppHttpRequestWrapper(request as HttpServletRequest)
        val responseWrapper = AppHttpResponseWrapper(response as HttpServletResponse)
        val apiSessionContext = ApiSessionContext(requestWrapper, responseWrapper)
        log.info("Start apiSessionContext to endpoint [${requestWrapper.wrapperEndpoint}] with transactionId [${apiSessionContext.transactionId}]")
        sysCallLogService.saveContextToSysCallLog(apiSessionContext)

        try {
            // Execute request
            chain.doFilter(requestWrapper, responseWrapper)
            responseWrapper.flushBuffer()
        } finally {
            // Save api-session to database and clear context
            apiSessionContext.endDt = Date()
            val transactionId = apiSessionContext.transactionId
            sysCallLogService.saveContextToSysCallLog(apiSessionContext)
            ApiSessionContext.clearApiCallContext()
            log.info("Finish api-call-context with transactionId [{}]", transactionId)
        }

    }

}
