package com.example.apiboilerplate.utils

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.math.min


object WebUtil {

    fun getIp(request: HttpServletRequest): String {
        val ipFromHeader: String? = request.getHeader("X-FORWARDED-FOR")
        if (ipFromHeader != null && ipFromHeader.isNotEmpty()) {
            // log.debug("ip from proxy - X-FORWARDED-FOR : $ipFromHeader")
            return ipFromHeader
        }
        return request.remoteAddr
    }

    fun extractRequestHeaders(request: HttpServletRequest): Map<String, String>? {
        val headerNames: Enumeration<String> = request.headerNames ?: return null
        val map: MutableMap<String, String> = HashMap()

        while (headerNames.hasMoreElements()) {
            val key = headerNames.nextElement() as String
            val value = request.getHeader(key)
            map[key] = value
        }

        return map
    }

    fun extractRequestParameters(request: HttpServletRequest): Map<String, String>? {
        val parameterNames: Enumeration<String> = request.parameterNames ?: return null
        val map: MutableMap<String, String> = HashMap()

        while (parameterNames.hasMoreElements()) {
            val key = parameterNames.nextElement() as String
            val value = request.getParameter(key)
            map[key] = value
        }

        return map
    }

    private fun getWrapperByteBufferPayload(buf: ByteArray, charEncoding: Charset): String? {
        var wrapperPayload: String? = null
        if (buf.isNotEmpty()) {
            val length = min(buf.size, 5120)
            try {
                wrapperPayload = String(buf, 0, length, charEncoding)
            } catch (ex: UnsupportedEncodingException) {
                // NOOP
            }
        }
        return wrapperPayload
    }

}