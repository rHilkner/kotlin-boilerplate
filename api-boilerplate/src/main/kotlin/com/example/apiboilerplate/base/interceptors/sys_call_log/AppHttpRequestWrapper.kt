package com.example.apiboilerplate.base.interceptors.sys_call_log

import com.example.apiboilerplate.base.logger.LoggerDelegate
import org.apache.commons.io.IOUtils
import org.springframework.util.StringUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class AppHttpRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    companion object { private val log by LoggerDelegate() }

    private var buffer: ByteArray?

    val wrapperIpAddress: String
        get() {
            val ip: String? = getHeader("X-FORWARDED-FOR")
            return if (StringUtils.hasText(ip)) ip!! else request.remoteAddr
        }
    val wrapperMethod: String
        get() = method
    val wrapperUrl: String
        get() = requestURL.toString()
    val wrapperEndpoint: String
        get() = requestURI.toString()
    val wrapperBody: String?
        get() = this.buffer?.let { String(it).replace("\n".toRegex(), " ") }
    val wrapperParameters: String?
        get() {
            val parameterNames: Enumeration<String> = parameterNames ?: return null
            val map: MutableMap<String, String> = HashMap()

            while (parameterNames.hasMoreElements()) {
                val key = parameterNames.nextElement() as String
                val value = getParameter(key)
                map[key] = value
            }

            return if (map.isEmpty()) null else map.toString()
        }
    val wrapperHeaders: String?
        get() {
            val map: MutableMap<String, String> = HashMap()
            for (key in headerNames) {
                map[key] = getHeader(key)
            }
            return if (map.isEmpty()) null else map.toString()
        }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        return ServletInputStreamCopier(ByteArrayInputStream(buffer ?: byteArrayOf()))
    }

    init {
        buffer = try {
            IOUtils.toByteArray(request.inputStream)
        } catch (e: IOException) {
            log.warn("request input stream not found")
            null
        }
    }

}