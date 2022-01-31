package com.example.apiboilerplate.base.filters.request_context

import com.example.apiboilerplate.base.ServiceContext
import com.example.apiboilerplate.base.logger.LoggerDelegate
import com.example.apiboilerplate.services.CallLogService
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * @author - GreenLearner(https://www.youtube.com/c/greenlearner)
 */
@Component
@Slf4j //@Order(1)
class ServiceContextFilter(
    private val callLogService: CallLogService
) : Filter {

    companion object { private val log by LoggerDelegate() }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {

        if (response.characterEncoding == null) {
            response.characterEncoding = "UTF-8"; // Or whatever default. UTF-8 is good for World Domination.
        }

        // Get request URI, method (GET, POST, ...) and body
        val requestWrapper = AppHttpRequestWrapper(request as HttpServletRequest)
        val responseWrapper = AppHttpResponseWrapper(response as HttpServletResponse)
        val ctx = ServiceContext(requestWrapper, responseWrapper)
        callLogService.saveContextToSysCallLog(ctx)

        try {
            chain.doFilter(requestWrapper, responseWrapper)
            responseWrapper.flushBuffer()
        } finally {
            ctx.endDt = Date()
            callLogService.saveContextToSysCallLog(ctx)
        }

    }

}
