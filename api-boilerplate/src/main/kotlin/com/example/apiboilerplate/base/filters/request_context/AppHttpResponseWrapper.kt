package com.example.apiboilerplate.base.filters.request_context

import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper


class AppHttpResponseWrapper(response: HttpServletResponse) : HttpServletResponseWrapper(response) {

    private var outputStream: ServletOutputStream? = null
    private var printWriter: PrintWriter? = null
    private var servletOutputStreamCopier: ServletOutputStreamCopier? = null

    init {
        outputStream = response.outputStream
        servletOutputStreamCopier = outputStream?.let { ServletOutputStreamCopier(it) }
    }

    val wrapperHttpStatus: Int
        get() = status
    val wrapperBody: String?
        get() {
            val outputStreamCopy = servletOutputStreamCopier?.getCopy()
            return outputStreamCopy?.let { String(it, charset(response.characterEncoding)) }
        }
    val wrapperHeaders: String?
        get() {
            val map: MutableMap<String, String> = HashMap()
            headerNames.stream().forEach { key -> map[key] = getHeader(key) }
            return if (map.isEmpty()) null else map.toString()
        }

//    @Throws(IOException::class)
//    override fun getOutputStream(): ServletOutputStream {
//        return ServletOutputStreamCopier(TeeOutputStream(super.getOutputStream(), printStream))
//    }

    @Throws(IOException::class)
    override fun getOutputStream(): ServletOutputStream? {
        check(printWriter == null) { "getWriter() has already been called on this response." }
        return servletOutputStreamCopier
    }

//    @Throws(IOException::class)
//    override fun getWriter(): PrintWriter {
//        return PrintWriter(TeeOutputStream(super.getOutputStream(), printStream))
//    }

    @Throws(IOException::class)
    override fun getWriter(): PrintWriter? {
        check(outputStream == null) { "getOutputStream() has already been called on this response." }
        if (printWriter == null) {
            servletOutputStreamCopier = ServletOutputStreamCopier(response.outputStream)
            printWriter = PrintWriter(OutputStreamWriter(servletOutputStreamCopier, response.characterEncoding), true)
        }
        return printWriter
    }

    @Throws(IOException::class)
    override fun flushBuffer() {
        if (printWriter != null) {
            printWriter!!.flush()
        } else if (outputStream != null) {
            servletOutputStreamCopier!!.flush()
        }
    }

}