package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.AppPaths
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import com.example.apiboilerplate.services.base.StorageService
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AppUserService(
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository,
    private val storageService: StorageService
) {

    companion object { private val log by ApiLogger() }

    fun getCurrentUserFromDb(cached: Boolean = false): AppUser? {

        if (cached && ApiSessionContext.getCurrentApiCallContext().currentUser != null) {
            return ApiSessionContext.getCurrentApiCallContext().currentUser
        }

        log.debug("Selecting current session's user from database")

        // Check if there is a current user
        val userId = ApiSessionContext.getCurrentApiCallContext().currentUserId ?: return null
        val userRole = ApiSessionContext.getCurrentApiCallContext().currentUserRole
            ?: throw ApiExceptionModule.General.UnexpectedException("User role not found")

        // Get user from database
        val appUser = when (userRole) {
            UserRole.ADMIN -> appAdminRepository.getById(userId)
            UserRole.CUSTOMER -> appCustomerRepository.getById(userId)
        }

        // Update user
        val appUserUpdated = updateLastActivityDt(appUser)
        ApiSessionContext.getCurrentApiCallContext().currentUser = appUserUpdated

        log.debug("Current session's user with id [${appUserUpdated.userId}] selected from database")

        return appUser
    }

    fun saveUser(appUser: AppUser): AppUser {
        return when (appUser.role) {
            UserRole.ADMIN -> appAdminRepository.save(appUser as AppAdmin)
            UserRole.CUSTOMER -> appCustomerRepository.save(appUser as AppCustomer)
        }
    }

    fun updateLastActivityDt(appUser: AppUser): AppUser {
        log.debug("Updating lastActivityDt of user [${appUser.userId}]")
        appUser.lastAccessDt = Date()
        val updatedAppUser = saveUser(appUser)
        log.debug("lastActivityDt updated for user [${appUser.userId}]")
        return updatedAppUser
    }

    fun uploadProfileImage(file: MultipartFile) {
        if (file.isEmpty) {
            log.error("Cannot upload empty file")
            throw ApiExceptionModule.General.BadRequestException("Cannot upload empty file")
        }

        // Throw exception if file is not an image
        if (!listOf(MediaType.IMAGE_JPEG.toString(), MediaType.IMAGE_PNG.toString()).contains(file.contentType)) {
            log.error("File provided is not a JPEG or PNG image")
            throw ApiExceptionModule.General.BadRequestException("File provided is not a JPEG or PNG image")
        }

        // Add metadata to file
        val metadata: MutableMap<String, String> = HashMap()
        metadata["Content-Type"] = file.contentType.toString()
        metadata["Content-Length"] = file.size.toString()

        // Save image to server's internal storage
        val filePath = storageService.getCompletePathFor(file.name, AppPaths.CUSTOMER_PROFILE_IMAGES)
        storageService.save(file, filePath)

        // Save profile-image path to user
        val appUser = this.getCurrentUserFromDb()!!
        appUser.profileImagePath = filePath
        this.saveUser(appUser)
    }

}