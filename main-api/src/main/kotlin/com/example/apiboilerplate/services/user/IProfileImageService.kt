package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.BaseLoginResponseDTO
import com.example.apiboilerplate.dtos.users.BaseUserProfileDTO
import com.example.apiboilerplate.enums.AppImageType
import com.example.apiboilerplate.enums.AppPaths
import com.example.apiboilerplate.models.user.BaseUserProfileWithImage
import com.example.apiboilerplate.services.ImageService
import com.example.apiboilerplate.services.base.StorageService
import org.springframework.web.multipart.MultipartFile


interface IProfileImageService<Model: BaseUserProfileWithImage, Dto: BaseUserProfileDTO> {

    val profileService: IUserProfileService<Model, Dto, out BaseLoginResponseDTO>
    val imageService: ImageService
    val storageService: StorageService

    companion object { private val log by ApiLogger() }

    fun saveCurrentUserProfileImage(file: MultipartFile): Model {
        val userProfile = profileService.getCurrentOrThrow()
        return saveUserProfileImage(file, userProfile)
    }

    fun downloadCurrentUserProfileImage(): ByteArray? {
        val userProfile = profileService.getCurrentOrThrow()
        return downloadUserProfileImage(userProfile)
    }

    // Save image to server's internal storage
    private fun saveUserProfileImage(file: MultipartFile, profile: Model): Model {
        val profileImagePath = formatAndStoreProfileImage(file, profile.userId)
        profile.profileImagePath = profileImagePath
        return profileService.save(profile)
    }

    private fun downloadUserProfileImage(profile: Model): ByteArray? {
        return profile.profileImagePath?.let { storageService.downloadImage(it) }
    }

    private fun formatAndStoreProfileImage(imageFile: MultipartFile, userId: Long): String {
        // Save image to server's internal storage
        log.info("Saving user [${userId}] profile image")
        val fileDirectory = AppPaths.getProfileImageDirectory(userId)
        val fileName = "profile_image.jpg"
        val optimizedImage = imageService.formatImage(imageFile.bytes, AppImageType.PROFILE)
        return storageService.saveImage(optimizedImage, fileDirectory, fileName)
    }

}