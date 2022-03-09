package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.ResetPasswordRequest
import com.example.apiboilerplate.enums.*
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import com.example.apiboilerplate.services.base.ApiSessionService
import com.example.apiboilerplate.services.base.AuthService
import com.example.apiboilerplate.services.base.EmailService
import com.example.apiboilerplate.services.base.StorageService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AppUserService(
    private val authService: AuthService,
    private val apiSessionService: ApiSessionService,
    private val emailService: EmailService,
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository,
    private val imageService: ImageService,
    private val storageService: StorageService
) {

    companion object { private val log by ApiLogger() }

    fun saveUser(appUser: AppUser): AppUser {
        return when (appUser.role) {
            UserRole.ADMIN -> appAdminRepository.save(appUser as AppAdmin)
            UserRole.CUSTOMER -> appCustomerRepository.save(appUser as AppCustomer)
        }
    }

    fun getCurrentUserOrThrow(): AppUser {
        return getCurrentUser()
            ?: throw ApiExceptionModule.General.NullPointer("Current session user is null")
    }

    fun getCurrentUser(cached: Boolean = false): AppUser? {

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

    fun getUserByEmail(email: String, userRole: UserRole): AppUser? {
        return when (userRole) {
            UserRole.ADMIN -> appAdminRepository.findAppAdminByEmail(email)
            UserRole.CUSTOMER -> appCustomerRepository.findAppCustomerByEmail(email)
        }
    }

    fun forgotPassword(email: String, userRole: UserRole) {
        log.info("User [$email] forgot password")

        // Get user from repository
        val appUser = getUserByEmail(email, userRole)
            ?: throw ApiExceptionModule.User.UserNotFoundException(email)

        // Create new api-session with RESET_PASSWORD permission
        val sessionToken = authService.generateNewSessionToken()
        apiSessionService.createAndSaveApiSession(appUser, sessionToken, listOf(Permission.RESET_PASSWORD), false)

        // Send to user's email session token to reset password
        emailService.send(
            AppEmails.ADMIN, email, "Forgot password",
            "To change password use session-token: $sessionToken")

        log.debug("Sent email to recover password to user [${appUser.role} / ${appUser.email}]")
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        val currentUser = getCurrentUserOrThrow()

        // Check if old-password matches current password
        if (!authService.passwordMatchesEncoded(resetPasswordRequest.oldPassword, currentUser.passwordHash)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }

        log.info("Changing user password with email [${currentUser.email}]")
        currentUser.passwordHash = authService.encodePassword(resetPasswordRequest.newPassword)
        this.saveUser(currentUser)
        log.debug("Changed user password with email [${currentUser.email}]")
    }

    fun forceResetPassword(newPassword: String) {

        // Double check if current session has ResetPassword permission - this was probably checked at controller level as well
        val currentPermissions = ApiSessionContext.getCurrentApiCallContext().apiSession!!.permissions
        if (currentPermissions.contains(Permission.RESET_PASSWORD)) {
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(currentPermissions, ApiSessionContext.getCurrentApiCallContext().request.method)
        }

        // Change password for user of current session
        val currentUser = getCurrentUserOrThrow()
        log.info("Changing user password with email [${currentUser.email}]")
        currentUser.passwordHash = authService.encodePassword(newPassword)
        saveUser(currentUser)
        log.debug("Changed user password with email [${currentUser.email}]")

        // Inactivate current session after successful password reset
        apiSessionService.inactivateCurrentSession()
    }

    fun updateLastLoginDt(appUser: AppUser): AppUser {
        log.debug("Updating lastLoginDt of user [${appUser.userId}]")
        appUser.lastLoginDt = Date()
        val updatedAppUser = saveUser(appUser)
        log.debug("lastLoginDt updated for user [${appUser.userId}]")
        return updatedAppUser
    }

    fun updateLastActivityDt(appUser: AppUser): AppUser {
        log.debug("Updating lastActivityDt of user [${appUser.userId}]")
        appUser.lastAccessDt = Date()
        val updatedAppUser = saveUser(appUser)
        log.debug("lastActivityDt updated for user [${appUser.userId}]")
        return updatedAppUser
    }

    fun uploadProfileImage(file: MultipartFile) {
        // Save image to server's internal storage
        val appUser = this.getCurrentUserOrThrow()
        log.info("Saving user [${appUser.userId}] profile image")
        val fileDirectory = AppPaths.getProfileImageDirectory(appUser.userId!!, appUser.role)
        val fileName = "profile_image.jpg"
        val optimizedImage = imageService.formatImage(file.bytes, AppImageType.PROFILE)
        storageService.saveImage(optimizedImage, fileDirectory, fileName)

        // Save profile-image path to user
        val fullPath = fileDirectory+fileName
        appUser.profileImagePath = fullPath
        this.saveUser(appUser)
        log.debug("User [${appUser.userId}] profile picture saved successfully at [$fullPath]")
    }

    fun downloadCurrentUserProfileImage(): ByteArray? {
        val appUser = this.getCurrentUserOrThrow()
        log.info("Downloading user [${appUser.userId}] profile image")
        return appUser.profileImagePath?.let { storageService.downloadImage(it) }
    }

}