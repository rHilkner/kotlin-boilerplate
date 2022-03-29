package com.example.apiboilerplate.services.user

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.ResetPasswordRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.enums.AppEmails
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.mappers.user.AppUserMapper
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.repositories.user.AppUserRepository
import com.example.apiboilerplate.services.base.ApiSessionService
import com.example.apiboilerplate.services.base.AuthService
import com.example.apiboilerplate.services.base.EmailService
import com.example.apiboilerplate.utils.RandomUtil
import com.example.apiboilerplate.validators.EmailValidator
import com.example.apiboilerplate.validators.PasswordValidator
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppUserService(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val authService: AuthService,
    private val apiSessionService: ApiSessionService,
    private val emailService: EmailService,
    private val appUserRepository: AppUserRepository
) {

    companion object { private val log by ApiLogger() }

    private val appUserMapper = AppUserMapper()

    /************************ APP USER REPOSITORY ************************/

    fun save(appUser: AppUser): AppUser {
        appUser.lastAccessIp = ApiSessionContext.getCurrentApiCallContext().request.wrapperIpAddress
        return appUserRepository.save(appUser)
    }

    fun getByUserUuidOrThrow(userUuid: UUID): AppUser {
        return appUserRepository.findByUserUuid(userUuid) ?: throw ApiExceptionModule.User.UserNotFoundException(userUuid)
    }

    fun getByIdOrThrow(userId: Long): AppUser {
        return appUserRepository.findByUserIdAndDeletedStatusFalse(userId) ?: throw ApiExceptionModule.User.UserNotFoundException(userId)
    }

    fun getByEmail(email: String, userRole: UserRole): AppUser? {
        return appUserRepository.findByEmailAndRoleAndDeletedStatusFalse(email, userRole)
    }

    /************************ AUTH & HIGH SECURITY ************************/

    fun login(loginRequestDTO: LoginRequestDTO, userRole: UserRole): AppUser {
        log.info("Login in [${userRole}] user with email [${loginRequestDTO.email}]")

        // Get user from database
        val appUser = this.getUserByEmailOrThrow(loginRequestDTO.email, userRole)
        // Login user
        authService.authenticate(appUser, loginRequestDTO.password)
        appUser.lastLoginDt = Date()
        val updatedAppUser = save(appUser)
        log.info("User [${userRole}] logged in successfully with email [${loginRequestDTO.email}]")

        return updatedAppUser
    }

    fun signUp(signUpRequestDTO: SignUpRequestDTO, userRole: UserRole): AppUser {
        log.info("Signing up new user [${userRole}] with email [${signUpRequestDTO.email}]")

        // Validate and encode password
        passwordValidator.passwordFormat(signUpRequestDTO.password)

        // Validate user information
        emailValidator.emailFormat(signUpRequestDTO.email)
        emailValidator.emailNotAlreadyUsed(signUpRequestDTO.email, userRole)

        // Create new user
        val passwordHash = authService.encodePassword(signUpRequestDTO.password)
        var appUser = appUserMapper.signUpDtoToAppUser(signUpRequestDTO, RandomUtil.generateRandomUUID(), userRole, passwordHash)
        appUser.signUpDt  = Date()
        appUser = this.save(appUser)
        log.info("New user [${userRole}] was created with email [${appUser.email}] and id [${appUser.userId}]")

        // Authenticate user
        val apiSession = authService.authenticate(appUser, signUpRequestDTO.password)
        log.info("New user [${userRole}] authenticated with email [${appUser.email}] with token [${apiSession.token}]")

        return appUser
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

    fun resetPassword(resetPasswordRequestDTO: ResetPasswordRequestDTO) {
        val appUser = getCurrentUserOrThrow()

        // Check if old-password matches current password
        if (!authService.passwordMatchesEncoded(resetPasswordRequestDTO.oldPassword, appUser.passwordHash)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }

        log.info("Changing user password with email [${appUser.email}]")
        appUser.passwordHash = authService.encodePassword(resetPasswordRequestDTO.newPassword)
        this.save(appUser)
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
        save(appUser)
        log.debug("Changed user password with email [${appUser.email}]")

        // Inactivate current session after successful password reset
        apiSessionService.inactivateCurrentSession()
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
        var appUser = getByIdOrThrow(userId)

        // Update user last access date
        appUser.lastAccessDt = Date()
        appUser = save(appUser)
        // Set current-user on api-session-context
        ApiSessionContext.getCurrentApiCallContext().currentUser = appUser

        log.debug("Current session's user selected from database with role [${appUser.role}] and id [${appUser.userId}]")

        return appUser
    }

    fun getUserByEmailOrThrow(email: String, userRole: UserRole): AppUser {
        return getByEmail(email, userRole) ?: throw ApiExceptionModule.User.UserNotFoundException(email)
    }

    fun softDeleteUser(userUuid: UUID) {
        log.info("Deleting user with id [$userUuid]")
        val appUser = getByUserUuidOrThrow(userUuid)

        appUser.deletedStatus = true
        appUser.deletedBy = getCurrentUserOrThrow().userId.toString()
        appUser.deletedDt = Date()

        save(appUser)
    }

}