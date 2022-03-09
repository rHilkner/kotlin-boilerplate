package com.example.apiboilerplate.services

import com.example.apiboilerplate.enums.AppImageType
import com.example.apiboilerplate.enums.FileExtension
import com.example.apiboilerplate.utils.FileUtil
import org.springframework.stereotype.Service

@Service
class ImageService {

    fun formatImage(imageBytes: ByteArray, imageType: AppImageType): ByteArray {

        when (imageType) {
            AppImageType.PROFILE -> {
                return formatImage(imageBytes, FileExtension.JPEG, 1024, 0.7)
            }
        }

    }

    /** targetSize – The target width and height (square) that you wish the image to fit within. */
    private fun formatImage(imageBytes: ByteArray, extension: FileExtension, targetSize: Int, quality: Double): ByteArray {
        val resizedImage = FileUtil.resizeImageKeepRatio(imageBytes, extension, targetSize)
        val convertedImage = FileUtil.convertImageBytes(resizedImage, extension)
        val compressedImage = FileUtil.compressImageBytes(convertedImage, extension, quality)
        return compressedImage
    }

}