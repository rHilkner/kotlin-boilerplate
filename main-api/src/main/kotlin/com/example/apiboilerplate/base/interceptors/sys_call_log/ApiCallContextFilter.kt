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
            response.characterEncoding = "UTF-8" // Or whatever default. UTF-8 is good for World Domination.
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
        } catch (e: Exception) {
            log.error("Error not handled from request: ", e)
            throw e
        } finally {
            // NOTE: there's been spot an error at org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.writeInternal(AbstractJackson2HttpMessageConverter.java:454
            // ... in which when a DTO var is a `lateinit` and null at the same time, an error occurs in the conversion
            // ... between Java Object and JSON inside the line 124, but no error is thrown! The error is only set in
            // ... the response, but never thrown. Here below we will handle this kind of situation
            // If response has error status but apiSessionContext.apiException is null, then something really weird happened, let's log this error
            if (responseWrapper.wrapperHttpStatus != 200 && apiSessionContext.apiException == null) {
                log.error("API response has error status but apiSessionContext.apiException is null -- check for lateinit vars in response object")
            }

            // Save api-session to database and clear context
            apiSessionContext.endDt = Date()
            val transactionId = apiSessionContext.transactionId
            sysCallLogService.saveContextToSysCallLog(apiSessionContext)
            // ApiSessionContext.clearApiCallContext() -> Called at AuthInterceptor.postHandle() to handle corner cases
            log.info("Finish api-call-context with transactionId [{}]", transactionId)
        }

    }

}
