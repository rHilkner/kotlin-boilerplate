package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.converters.ApiSessionConverter
import com.example.apiboilerplate.converters.AppUserConverter
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.dtos.users.AdminDTO
import com.example.apiboilerplate.dtos.users.AppUserDTO
import com.example.apiboilerplate.dtos.users.CustomerDTO
import com.example.apiboilerplate.enums.AppEmails
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.FullUser
import com.example.apiboilerplate.repositories.user.AppUserRepository
import com.example.apiboilerplate.services.base.ApiSessionService
import com.example.apiboilerplate.services.base.AuthService
import com.example.apiboilerplate.services.base.EmailService
import com.example.apiboilerplate.services.base.SecurityService
import com.example.apiboilerplate.validators.EmailValidator
import com.example.apiboilerplate.validators.PasswordValidator
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppUserService(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val authService: AuthService,
    private val apiSessionService: ApiSessionService,
    @Lazy
    private val userProfileService: UserProfileService,
    private val emailService: EmailService,
    private val appUserRepository: AppUserRepository,
    private val securityService: SecurityService
) {

    companion object { private val log by ApiLogger() }

    private val apiSessionConverter = ApiSessionConverter()
    private val appUserConverter = AppUserConverter()

    /************************ APP USER REPOSITORY ************************/

    fun saveAppUser(appUser: AppUser): AppUser {
        appUser.lastAccessIp = ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress
        return appUserRepository.save(appUser)
    }

    fun getUserByIdOrThrow(userId: Long): AppUser {
        return appUserRepository.findByUserIdAndDeletedStatusFalse(userId) ?: throw ApiExceptionModule.User.UserNotFoundException(userId)
    }

    fun getUserByEmail(email: String, userRole: UserRole): AppUser? {
        return appUserRepository.findByEmailAndRoleAndDeletedStatusFalse(email, userRole)
    }

    /************************ AUTH & HIGH SECURITY ************************/

    fun login(loginRequestDTO: LoginRequestDTO, userRole: UserRole): LoginResponseDTO {
        log.info("Login in [${userRole}] user with email [${loginRequestDTO.email}]")

        // Get user from database
        val appUser = this.getUserByEmailOrThrow(loginRequestDTO.email, userRole)
        // Login user
        val apiSession = authService.authenticate(appUser, loginRequestDTO.password)
        appUser.lastLoginDt = Date()
        val updatedAppUser = saveAppUser(appUser)
        // Get user with profile
        val fullUser = userProfileService.getFullUserOrThrow(updatedAppUser)

        // If no error was thrown, return response dto
        log.info("User [${userRole}] logged in successfully with email [${loginRequestDTO.email}]")
        return this.buildLoginResponseDTO(fullUser, apiSession)
    }

    fun signUp(signUpRequestDTO: SignUpRequestDTO, userRole: UserRole): LoginResponseDTO {
        log.info("Signing up new user [${userRole}] with email [${signUpRequestDTO.email}]")

        // Validate and encode password
        passwordValidator.passwordFormat(signUpRequestDTO.password)

        // Validate user information
        emailValidator.emailFormat(signUpRequestDTO.email)
        emailValidator.emailNotAlreadyUsed(signUpRequestDTO.email, userRole)

        // Create new user
        val passwordHash = authService.encodePassword(signUpRequestDTO.password)
        var appUser = appUserConverter.signUpDtoToAppUser(signUpRequestDTO, generateRandomUUID(), userRole, passwordHash)
        appUser.signUpDt  = Date()
        appUser = this.saveAppUser(appUser)
        log.info("New user [${userRole}] was created with email [${appUser.email}] and id [${appUser.userId}]")

        // Create user profile
        var userProfile = appUserConverter.signUpDtoToUserProfile(signUpRequestDTO, appUser)
        userProfile = userProfileService.saveUserProfile(userProfile)

        // Instantiate FullUser object
        val fullUser = appUserConverter.buildFullUser(appUser, userProfile)

        // Authenticate user
        val apiSession = authService.authenticate(appUser, signUpRequestDTO.password)
        log.info("New user [${userRole}] authenticated with email [${appUser.email}] with token [${apiSession.token}]")

        // If no error was thrown, return response dto
        return this.buildLoginResponseDTO(fullUser, apiSession)
    }

    fun forgotPassword(email: String, userRole: UserRole) {
        log.info("User [$email] forgot password")

        // Get user from repository
        val appUser = getUserByEmailOrThrow(email, userRole)

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
        val appUser = getCurrentUserOrThrow()

        // Check if old-password matches current password
        if (!authService.passwordMatchesEncoded(resetPasswordRequest.oldPassword, appUser.passwordHash)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }

        log.info("Changing user password with email [${appUser.email}]")
        appUser.passwordHash = authService.encodePassword(resetPasswordRequest.newPassword)
        this.saveAppUser(appUser)
        log.debug("Changed user password with email [${appUser.email}]")
    }

    fun forceResetPassword(newPassword: String) {

        // Double check if current session has ResetPassword permission - this was probably checked at controller level as well
        val currentPermissions = ApiSessionContext.getCurrentApiCallContext().apiSession!!.permissions
        if (currentPermissions.contains(Permission.RESET_PASSWORD)) {
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(currentPermissions, ApiSessionContext.getCurrentApiCallContext().request.method)
        }

        // Change password for user of current session
        val appUser = getCurrentUserOrThrow()
        log.info("Changing user password with email [${appUser.email}]")
        appUser.passwordHash = authService.encodePassword(newPassword)
        saveAppUser(appUser)
        log.debug("Changed user password with email [${appUser.email}]")

        // Inactivate current session after successful password reset
        apiSessionService.inactivateCurrentSession()
    }

    private fun buildLoginResponseDTO(user: FullUser, apiSession: ApiSession): LoginResponseDTO {
        val fullUserDto = appUserConverter.fullUserToFullUserDto(user)
        val apiSessionDto = apiSessionConverter.apiSessionToApiSessionResponseDto(apiSession)
        return when (user.appUser.role) {
            UserRole.ADMIN -> LoginAdminResponseDTO(fullUserDto as AdminDTO, apiSessionDto)
            UserRole.CUSTOMER -> LoginCustomerResponseDTO(fullUserDto as CustomerDTO, apiSessionDto)
        }
    }

    private fun generateRandomUUID(): UUID {
        return UUID.randomUUID()
    }

    /************************ APP USER ************************/

    fun getCurrentUserOrThrow(): AppUser {
        return getCurrentUser() ?: throw ApiExceptionModule.General.NullPointer("Current session user is null")
    }

    fun getCurrentUser(cached: Boolean = false): AppUser? {

        if (cached && ApiSessionContext.getCurrentApiCallContext().currentUser != null) {
            return ApiSessionContext.getCurrentApiCallContext().currentUser
        }

        log.debug("Selecting current session's user from database")

        // Check if there is a current user
        val userId = ApiSessionContext.getCurrentApiCallContext().currentUserId ?: return null

        // Get user from database
        var appUser = getUserByIdOrThrow(userId)

        // Update user last access date
        appUser.lastAccessDt = Date()
        appUser = saveAppUser(appUser)
        // Set current-user on api-session-context
        ApiSessionContext.getCurrentApiCallContext().currentUser = appUser

        log.debug("Current session's user selected from database with role [${appUser.role}] and id [${appUser.userId}]")

        return appUser
    }

    fun getUserByEmailOrThrow(email: String, userRole: UserRole): AppUser {
        return getUserByEmail(email, userRole) ?: throw ApiExceptionModule.User.UserNotFoundException(email)
    }

    fun getUserDtoByEmail(email: String, userRole: UserRole): AppUserDTO? {
        return getUserByEmail(email, userRole)?.let { appUserConverter.appUserToAppUserDto(it) }
    }

    fun deleteUser(userId: Long, userRole: UserRole) {
        securityService.verifyRoleForCurrentUser(userRole)
        log.info("Deleting user [${userRole}] with id [$userId]")
        val appUser = getUserByIdOrThrow(userId)

        appUser.deletedStatus = true
        appUser.deletedBy = getCurrentUserOrThrow().userId.toString()
        appUser.deletedDt = Date()

        saveAppUser(appUser)
    }

    /** Updatable fields are: name. Any other field that has been modified will be ignored.
     */
    fun updateUser(currentAppUser: AppUser, newAppUserDTO: AppUserDTO): AppUserDTO {
        log.info("Updating user [${currentAppUser.role}] with id [${newAppUserDTO.userId}]")

        // Updating common fields
        currentAppUser.name = newAppUserDTO.name

        // Persisting to database
        val newAppUser = this.saveAppUser(currentAppUser)
        log.info("Successfully updated user [${newAppUser.role}] with id [${newAppUser.userId}]")
        // Returning DTO
        return appUserConverter.appUserToAppUserDto(newAppUser)
    }

}