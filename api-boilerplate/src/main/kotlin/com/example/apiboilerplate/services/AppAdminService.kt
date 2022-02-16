package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.converters.ApiSessionConverter
import com.example.apiboilerplate.converters.AppAdminConverter
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.ResetPasswordRequest
import com.example.apiboilerplate.enums.AppEmails
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.services.base.ApiSessionService
import com.example.apiboilerplate.services.base.AuthService
import com.example.apiboilerplate.services.base.EmailService
import com.example.apiboilerplate.services.base.SecurityService
import org.springframework.stereotype.Service

@Service
class AppAdminService(
    private val validatorService: ValidatorService,
    private val appAdminRepository: AppAdminRepository,
    private val authService: AuthService,
    private val apiSessionService: ApiSessionService,
    private val appUserService: AppUserService,
    private val emailService: EmailService,
    private val securityService: SecurityService
) {

    companion object { private val log by ApiLogger() }

    private val appAdminConverter = AppAdminConverter()
    private val apiSessionConverter = ApiSessionConverter()

    fun login(loginRequestDTO: LoginRequestDTO): AuthAppAdminResponseDTO {
        log.info("Login in admin with email [${loginRequestDTO.email}]")

        // Get user from database
        val appAdmin = appAdminRepository.findAppAdminByEmail(loginRequestDTO.email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(loginRequestDTO.email)
        // Authenticate user
        val apiSession = authService.authenticate(appAdmin, loginRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppAdminResponseDTO(apiSessionConverter.apiSessionToApiSessionResponseDto(apiSession),
            appAdminConverter.appAdminToAppAdminDto(appAdmin))
    }

    fun signUp(adminSignUpRequestDTO: AdminSignUpRequestDTO): AuthAppAdminResponseDTO {
        log.info("Signing up new admin with email [${adminSignUpRequestDTO.email}]")

        // Validate and encode password
        validatorService.validatePassword(adminSignUpRequestDTO.password)

        // Validate user information
        validatorService.validateEmail(adminSignUpRequestDTO.email)
        validatorService.validateEmailAlreadyUsed(adminSignUpRequestDTO.email)

        // Create new user
        val passwordHash = authService.encodePassword(adminSignUpRequestDTO.password)
        var newAppAdmin = appAdminConverter.signUpDtoToAppAdmin(adminSignUpRequestDTO, passwordHash)
        newAppAdmin = appAdminRepository.save(newAppAdmin)
        log.info("New admin was created with email [${newAppAdmin.email}] and id [${newAppAdmin.adminId}]")

        // Authenticate user
        val apiSession = authService.authenticate(newAppAdmin, adminSignUpRequestDTO.password)
        log.info("New admin authenticated with email [${newAppAdmin.email}] with token [${apiSession.token}]")

        // If no error was thrown, return response dto
        return AuthAppAdminResponseDTO(apiSessionConverter.apiSessionToApiSessionResponseDto(apiSession),
            appAdminConverter.appAdminToAppAdminDto(newAppAdmin))
    }

    fun forgotPassword(email: String) {
        log.info("Admin [$email] forgot password")

        // Get user from repository
        val appAdmin = appAdminRepository.findAppAdminByEmail(email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(email)

        // Create new api-session with RESET_PASSWORD permission
        val sessionToken = authService.generateNewSessionToken()
        apiSessionService.createAndSaveApiSession(appAdmin, sessionToken, listOf(Permission.RESET_PASSWORD), false)

        // Send to user's email session token to reset password
        emailService.send(AppEmails.ADMIN, email, "Forgot password",
            "To change password use session-token: $sessionToken")

        log.debug("Sent email to recover password to user [${appAdmin.role} / ${appAdmin.email}]")
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        val currentAppAdmin = getCurrentAdmin()

        // Check if old-password matches current password
        if (!authService.passwordMatchesEncoded(resetPasswordRequest.oldPassword, currentAppAdmin.passwordHash)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }

        log.info("Changing admin password with email [${currentAppAdmin.email}]")
        currentAppAdmin.passwordHash = authService.encodePassword(resetPasswordRequest.newPassword)
        appAdminRepository.save(currentAppAdmin)
        log.debug("Changed admin password with email [${currentAppAdmin.email}]")
    }

    fun forceResetPassword(newPassword: String) {

        // Double check if current session has ResetPassword permission - this was probably checked at controller level as well
        val currentPermissions = ApiSessionContext.getCurrentApiCallContext().apiSession!!.permissions
        if (currentPermissions.contains(Permission.RESET_PASSWORD)) {
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(currentPermissions, ApiSessionContext.getCurrentApiCallContext().request.method)
        }

        // Change password for user of current session
        val currentAppAdmin = getCurrentAdmin()
        log.info("Changing admin password with email [${currentAppAdmin.email}]")
        currentAppAdmin.passwordHash = authService.encodePassword(newPassword)
        appAdminRepository.save(currentAppAdmin)
        log.debug("Changed admin password with email [${currentAppAdmin.email}]")

        // Inactivate current session after successful password reset
        apiSessionService.inactivateCurrentSession()
    }

    fun getCurrentAdmin(): AppAdmin {
        return appUserService.getCurrentUserFromDb() as AppAdmin
    }

    fun getCurrentAdminDto(): AppAdminDTO {
        log.debug("Getting admin for current session")
        return appAdminConverter.appAdminToAppAdminDto(getCurrentAdmin())
    }

    fun getAdminDtoByEmail(email: String): AppAdminDTO? {
        log.debug("Getting admin with email [$email]")
        val appAdmin = appAdminRepository.findAppAdminByEmail(email)
        return appAdmin?.let { appAdminConverter.appAdminToAppAdminDto(it) }
    }

    /** Updatable fields are: name, email, phone, documentId, address, addressComplement
     * Any other field that has been modified will be ignored
     */
    fun updateCurrentAdmin(newAppAdminDTO: AppAdminDTO): AppAdminDTO {
        val currentAppAdmin = appUserService.getCurrentUserFromDb() as AppAdmin

        // Verify if current user is trying to update some user other than himself
        if (currentAppAdmin.adminId != newAppAdminDTO.adminId) {
            log.error("User ${currentAppAdmin.adminId} does not have enough privileges to update user with id ${newAppAdminDTO.adminId}")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException("User ${currentAppAdmin.adminId} does not have enough privileges to update user with id ${newAppAdminDTO.adminId}")
        }
        return updateAdmin(currentAppAdmin, newAppAdminDTO)
    }

    fun updateAdmin(newAppAdminDTO: AppAdminDTO): AppAdminDTO {
        if (newAppAdminDTO.adminId == null) throw ApiExceptionModule.General.NullPointer("adminDTO.customerId")
        val currentAppAdmin = appAdminRepository.getById(newAppAdminDTO.adminId!!)
        return updateAdmin(currentAppAdmin, newAppAdminDTO)
    }

    private fun updateAdmin(currentAppAdmin: AppAdmin, newAppAdminDTO: AppAdminDTO): AppAdminDTO {
        log.info("Updating app-admin [${newAppAdminDTO.adminId}] information")

        // Updating
        currentAppAdmin.name = newAppAdminDTO.name
        currentAppAdmin.email = newAppAdminDTO.email
        // Persisting to database
        val newAppAdmin = appAdminRepository.save(currentAppAdmin)
        log.info("AppAdmin with id [${newAppAdmin.adminId}] updated")
        // Returning DTO
        return appAdminConverter.appAdminToAppAdminDto(newAppAdmin)

    }

    fun deleteAdmin(adminId: Long) {
        securityService.verifyRoleForCurrentUser(UserRole.ADMIN)
        log.debug("Deleting app-admin [$adminId]")
        appAdminRepository.deleteByAdminId(adminId)
    }

}