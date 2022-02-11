package com.example.apiboilerplate.base.interceptors.sys_call_log

import org.springframework.util.Assert
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener


class ServletOutputStreamCopier(targetStream: OutputStream) : ServletOutputStream() {

    private val outputStream: OutputStream
    private var copy = ByteArrayOutputStream(1024)

    init {
        Assert.notNull(targetStream, "Target OutputStream must not be null")
        this.outputStream = targetStream
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        outputStream.write(b)
        copy.write(b)
    }

    @Throws(IOException::class)
    override fun flush() {
        super.flush()
        outputStream.flush()
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
        outputStream.close()
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setWriteListener(writeListener: WriteListener) {
        throw UnsupportedOperationException()
    }

    fun getCopy(): ByteArray? {
        return if (copy.toByteArray().isEmpty()) null else copy.toByteArray()
    }

}