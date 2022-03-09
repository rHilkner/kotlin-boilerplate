package com.example.apiboilerplate.base.interceptors.sys_call_log

import org.springframework.util.Assert
import java.io.IOException
import java.io.InputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream


/**
 * @author - GreenLearner(https://www.youtube.com/c/greenlearner)
 */
class ServletInputStreamCopier(sourceStream: InputStream) : ServletInputStream() {

    private val inputStream: InputStream
    private var finished = false

    /**
     * Create a DelegatingServletInputStream for the given source stream.
     * @param sourceStream the source stream (never `null`)
     */
    init {
        Assert.notNull(sourceStream, "Source InputStream must not be null")
        this.inputStream = sourceStream
    }

    @Throws(IOException::class)
    override fun read(): Int {
        val data = inputStream.read()
        if (data == -1) {
            finished = true
        }
        return data
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return inputStream.available()
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
        inputStream.close()
    }

    override fun isFinished(): Boolean {
        return finished
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setReadListener(readListener: ReadListener) {
        throw UnsupportedOperationException()
    }

}
