package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.utils.FileUtil
import com.example.apiboilerplate.validators.FileValidator
import com.example.apiboilerplate.validators.StoragePermissionsValidator
import org.springframework.stereotype.Service


@Service
class StorageService(
    private val fileValidator: FileValidator,
    private val storagePermissionsValidator: StoragePermissionsValidator
) {

    companion object { private val log by ApiLogger() }

    fun downloadImage(path: String): ByteArray {
        log.info("Downloading image in path [$path]")
        storagePermissionsValidator.checkCurrentUserReadPermissions(path)
        val imageBytes = FileUtil.read(path)
        log.debug("Image downloaded from path [$path]")
        return imageBytes
    }

    fun saveImage(imageBytes: ByteArray, directory: String, fileName: String): String {
        fileValidator.fileNotEmpty(imageBytes)
        fileValidator.imageFile(imageBytes)
        storagePermissionsValidator.checkCurrentUserWritePermissions(directory)
        saveFile(imageBytes, directory, fileName)
        return directory + fileName
    }

    private fun saveFile(fileBytes: ByteArray, directory: String, fileName: String) {
        val fullPath = directory + fileName
        log.info("Saving file to [$fullPath]")
        val metadata = extractFileMetadata(fileBytes)
        FileUtil.write(fileBytes, metadata, directory, fileName)
        log.debug("File saved to [$fullPath]")
    }

    private fun extractFileMetadata(fileBytes: ByteArray): Map<String, String> {
        val metadata: MutableMap<String, String> = HashMap()
        metadata["Content-Type"] = FileUtil.extractByteArrayContentType(fileBytes)
        metadata["Content-Length"] = "${fileBytes.size}"
        return metadata
    }

}