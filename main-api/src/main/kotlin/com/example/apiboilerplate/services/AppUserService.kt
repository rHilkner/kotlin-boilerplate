package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.converters.ApiSessionConverter
import com.example.apiboilerplate.converters.AppUserConverter
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.enums.*
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import com.example.apiboilerplate.services.base.*
import com.example.apiboilerplate.validators.EmailValidator
import com.example.apiboilerplate.validators.PasswordValidator
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class AppUserService(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val authService: AuthService,
    private val apiSessionService: ApiSessionService,
    private val emailService: EmailService,
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository,
    private val imageService: ImageService,
    private val storageService: StorageService,
    private val securityService: SecurityService
) {

    companion object { private val log by ApiLogger() }

    private val apiSessionConverter = ApiSessionConverter()
    private val appUserConverter = AppUserConverter()

    fun login(loginRequestDTO: LoginRequestDTO, userRole: UserRole): LoginResponseDTO {
        log.info("Login in [${userRole}] user with email [${loginRequestDTO.email}]")

        // Get user from database
        val appUser = this.getUserByEmailOrThrow(loginRequestDTO.email, userRole)
        // Login user
        val apiSession = authService.authenticate(appUser, loginRequestDTO.password)
        appUser.lastLoginDt = Date()
        val updatedAppUser = saveUser(appUser)

        // If no error was thrown, return response dto
        log.info("User [${userRole}] logged in successfully with email [${loginRequestDTO.email}]")
        return this.buildLoginResponseDTO(updatedAppUser, apiSession)
    }

    fun signUp(signUpRequestDTO: SignUpRequestDTO, userRole: UserRole): LoginResponseDTO {
        log.info("Signing up new user [${userRole}] with email [${signUpRequestDTO.email}]")

        // Validate and encode password
        passwordValidator.passwordFormat(signUpRequestDTO.password)

        // Validate user information
        emailValidator.emailFormat(signUpRequestDTO.email)
        emailValidator.emailNotAlreadyUsed(signUpRequestDTO.email, UserRole.ADMIN)

        // Create new user
        val passwordHash = authService.encodePassword(signUpRequestDTO.password)
        var newAppUser = appUserConverter.signUpDtoToAppUser(signUpRequestDTO, passwordHash)
        newAppUser = this.saveUser(newAppUser)
        log.info("New user [${userRole}] was created with email [${newAppUser.email}] and id [${newAppUser.userId}]")

        // Authenticate user
        val apiSession = authService.authenticate(newAppUser, signUpRequestDTO.password)
        log.info("New user [${userRole}] authenticated with email [${newAppUser.email}] with token [${apiSession.token}]")

        // If no error was thrown, return response dto
        return this.buildLoginResponseDTO(newAppUser, apiSession)
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

    fun saveUser(appUser: AppUser): AppUser {
        return when (appUser.role) {
            UserRole.ADMIN -> appAdminRepository.save(appUser as AppAdmin)
            UserRole.CUSTOMER -> appCustomerRepository.save(appUser as AppCustomer)
        }
    }

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
        val userRole = ApiSessionContext.getCurrentApiCallContext().currentUserRole
            ?: throw ApiExceptionModule.General.UnexpectedException("User role not found")

        // Get user from database
        val appUser = when (userRole) {
            UserRole.ADMIN -> appAdminRepository.getById(userId)
            UserRole.CUSTOMER -> appCustomerRepository.getById(userId)
        }

        // Update user last access date
        appUser.lastAccessDt = Date()
        val updatedAppUser = saveUser(appUser)
        ApiSessionContext.getCurrentApiCallContext().currentUser = updatedAppUser

        log.debug("Current session's user selected from database: [${userRole}] with id [${updatedAppUser.userId}]")

        return appUser
    }

    fun getUserById(userId: Long, userRole: UserRole): AppUser? {
        return when (userRole) {
            UserRole.ADMIN -> appAdminRepository.getById(userId)
            UserRole.CUSTOMER -> appCustomerRepository.getById(userId)
        }
    }

    fun getUserByIdOrThrow(userId: Long, userRole: UserRole): AppUser {
        return this.getUserById(userId, userRole) ?: throw ApiExceptionModule.User.UserNotFoundException(userId)
    }

    fun getUserByEmail(email: String, userRole: UserRole): AppUser? {
        return when (userRole) {
            UserRole.ADMIN -> appAdminRepository.findAppAdminByEmail(email)
            UserRole.CUSTOMER -> appCustomerRepository.findAppCustomerByEmail(email)
        }
    }

    fun getUserByEmailOrThrow(email: String, userRole: UserRole): AppUser {
        return this.getUserByEmail(email, userRole) ?: throw ApiExceptionModule.User.UserNotFoundException(email)
    }

    fun getCurrentUserDto(): AppUserDTO? {
        return this.getCurrentUser()?.let { appUserConverter.appUserToAppUserDto(it) }
    }

    fun getUserDtoByEmail(email: String, userRole: UserRole): AppUserDTO? {
        return this.getUserByEmail(email, userRole)?.let { appUserConverter.appUserToAppUserDto(it) }
    }

    fun updateCurrentUser(newUserDTO: AppUserDTO): AppUserDTO {
        val currentUser = this.getCurrentUserOrThrow()

        // Verify if current user is trying to update some user other than himself
        if (currentUser.userId != newUserDTO.userId) {
            log.error("User ${currentUser.userId} does not have enough privileges to update user with id ${newUserDTO.userId}")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(
                "User ${currentUser.userId} does not have enough privileges to update user with id ${newUserDTO.userId}")
        }
        return updateUser(currentUser, newUserDTO)
    }

    fun updateUser(newUserDTO: AppUserDTO): AppUserDTO {
        if (newUserDTO.userId == null) throw ApiExceptionModule.General.NullPointer("userDTO.userId")
        val appUser = this.getUserByIdOrThrow(newUserDTO.userId!!, newUserDTO.role)
        return updateUser(appUser, newUserDTO)
    }

    fun deleteUser(userId: Long, userRole: UserRole) {
        securityService.verifyRoleForCurrentUser(userRole)
        log.info("Deleting user [${userRole}] with id [$userId]")
        when (userRole) {
            UserRole.ADMIN -> appAdminRepository.deleteByAdminId(userId)
            UserRole.CUSTOMER -> appCustomerRepository.deleteByCustomerId(userId)
        }
    }

    /** Updatable fields are: name (any), email (any), phone (customer), documentId (customer), address (customer),
     * addressComplement (customer). Any other field that has been modified will be ignored.
     */
    private fun updateUser(currentAppUser: AppUser, newAppUserDTO: AppUserDTO): AppUserDTO {
        log.info("Updating user [${currentAppUser.role}] with id [${newAppUserDTO.userId}]")

        // Updating common fields
        currentAppUser.name = newAppUserDTO.name
        currentAppUser.email = newAppUserDTO.email

        if (currentAppUser.role == UserRole.CUSTOMER) {
            // Casting user objs as customer objs
            currentAppUser as AppCustomer
            newAppUserDTO as AppCustomerDTO
            // Updating fields
            currentAppUser.phone = newAppUserDTO.phone
            currentAppUser.documentId = newAppUserDTO.documentId
            currentAppUser.address = newAppUserDTO.address
            currentAppUser.addressComplement = newAppUserDTO.addressComplement
        }

        // Persisting to database
        val newAppUser = this.saveUser(currentAppUser)
        log.info("Successfully updated user [${newAppUser.role}] with id [${newAppUser.userId}]")
        // Returning DTO
        return appUserConverter.appUserToAppUserDto(newAppUser)
    }

    private fun buildLoginResponseDTO(appUser: AppUser, apiSession: ApiSession): LoginResponseDTO {
        val apiSessionDto = apiSessionConverter.apiSessionToApiSessionResponseDto(apiSession)
        val appUserDto = appUserConverter.appUserToAppUserDto(appUser)
        return when (appUser.role) {
            UserRole.ADMIN -> LoginAppAdminResponseDTO(appUserDto as AppAdminDTO, apiSessionDto)
            UserRole.CUSTOMER -> LoginAppCustomerResponseDTO(appUserDto as AppCustomerDTO, apiSessionDto)
        }
    }

}