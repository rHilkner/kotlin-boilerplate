package com.example.apiboilerplate.utils

import com.example.apiboilerplate.enums.FileExtension
import org.apache.commons.io.FileUtils
import org.imgscalr.Scalr
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLConnection
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter


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

    fun convertImageBytes(imageBytes: ByteArray, extension: FileExtension): ByteArray {
        // If image is already PNG, just return it
        if (extractByteArrayContentType(imageBytes) == "image/${extension.extension}") {
            return imageBytes
        }

        // Converting imageBytes to bufferedImage, converting to desired extension, then converting back to byteArray
        val bufferedImage = byteArrayToBufferedImage(imageBytes)
        return bufferedImageToByteArray(bufferedImage, extension)
    }

    /** targetSize: The target width and height (square) that you wish the image to fit within. */
    fun resizeImageKeepRatio(imageBytes: ByteArray, extension: FileExtension, targetSize: Int): ByteArray {
        val bufferedImage = byteArrayToBufferedImage(imageBytes)
        val resizedBufferedImage =  Scalr.resize(bufferedImage, targetSize)
        return bufferedImageToByteArray(resizedBufferedImage, extension)
    }

    // 0.0 <= quality <= 1.0
    fun compressImageBytes(imageBytes: ByteArray, extension: FileExtension, quality: Double): ByteArray {
        val baos = ByteArrayOutputStream()
        val ios = ImageIO.createImageOutputStream(baos)
        val bufferedImage = byteArrayToBufferedImage(imageBytes)

        val writer = ImageIO.getImageWritersByFormatName(extension.extension).next() as ImageWriter

        val param = writer.defaultWriteParam
        param.compressionMode = ImageWriteParam.MODE_EXPLICIT
        param.compressionQuality = quality.toFloat()

        writer.output = ios
        writer.write(null, IIOImage(bufferedImage, null, null), param)

        ios.close()
        baos.close()
        writer.dispose()

        return bufferedImageToByteArray(bufferedImage, extension)
    }

    private fun byteArrayToBufferedImage(imageBytes: ByteArray): BufferedImage {
        // Read given byte-array as image
        val bais = ByteArrayInputStream(imageBytes)
        return ImageIO.read(bais)
    }

    private fun bufferedImageToByteArray(bufferedImage: BufferedImage, extension: FileExtension): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, extension.extension, baos)
        // Convert OutputStream to a byte-array
        return baos.toByteArray()
    }

}