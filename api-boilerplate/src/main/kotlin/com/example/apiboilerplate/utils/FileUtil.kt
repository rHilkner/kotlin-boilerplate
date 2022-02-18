package com.example.apiboilerplate.utils

import org.apache.commons.io.FileUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.createDirectories
import kotlin.io.path.exists


object FileUtil {

    fun read(path: String): ByteArray {
        return FileUtils.readFileToByteArray(File(path))
    }

    fun write(fileBytes: ByteArray, metadata: Map<String, String>, directory: String, fileName: String, createDirectory: Boolean = true) {
        val fileDirectory = File(directory)
        if (createDirectory && !File(directory).exists()) {
            // Create all parent directories if they don't already exist
            fileDirectory.mkdirs()
        }
        // Create file
        val file = File(directory+fileName)
        FileUtils.writeByteArrayToFile(file, fileBytes)
    }

    fun extractByteArrayContentType(byteArray: ByteArray): String {
        return URLConnection.guessContentTypeFromStream(ByteArrayInputStream(byteArray))
    }

    fun convertImageBytesToPng(imageBytes: ByteArray): ByteArray {
        // If image is already PNG, just return it
        if (extractByteArrayContentType(imageBytes) == "image/png") {
            return imageBytes
        }

        // Read given byte-array as image
        val bais = ByteArrayInputStream(imageBytes)
        val bufferedImage = ImageIO.read(bais)
        // Write image as PNG bytes
        val baos = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", baos)
        // Convert OutputStream to a byte-array
        return baos.toByteArray()
    }

}