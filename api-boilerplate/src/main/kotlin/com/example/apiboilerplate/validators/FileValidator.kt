package com.example.apiboilerplate.validators

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.utils.FileUtil
import org.springframework.stereotype.Component

@Component
class FileValidator {

    companion object { private val log by ApiLogger() }

    // Throw exception if file is empty
    fun fileNotEmpty(byteArray: ByteArray) {
        if (byteArray.isEmpty()) {
            log.error("File is empty")
            throw ApiExceptionModule.General.BadRequestException("File is empty")
        }
    }

    // Throw exception if file is not an image
    fun imageFile(byteArray: ByteArray) {
        val contentType = FileUtil.extractByteArrayContentType(byteArray)
        if (!listOf("images/png", "images/jpg").contains(contentType)) {
            log.error("File provided is not a JPEG nor PNG image")
            throw ApiExceptionModule.General.BadRequestException("File provided is not a JPEG nor PNG image")
        }
    }

}