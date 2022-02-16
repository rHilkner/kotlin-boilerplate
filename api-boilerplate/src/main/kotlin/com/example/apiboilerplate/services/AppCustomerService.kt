package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.converters.ApiSessionConverter
import com.example.apiboilerplate.converters.AppCustomerConverter
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.AuthAppCustomerResponseDTO
import com.example.apiboilerplate.dtos.auth.CustomerSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.ResetPasswordRequest
import com.example.apiboilerplate.enums.AppEmails
import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.repositories.AppCustomerRepository
import com.example.apiboilerplate.services.base.ApiSessionService
import com.example.apiboilerplate.services.base.AuthService
import com.example.apiboilerplate.services.base.EmailService
import com.example.apiboilerplate.services.base.SecurityService
import org.springframework.stereotype.Service

@Service
class AppCustomerService(
    private val validatorService: ValidatorService,
    private val appCustomerRepository: AppCustomerRepository,
    private val authService: AuthService,
    private val apiSessionService: ApiSessionService,
    private val appUserService: AppUserService,
    private val emailService: EmailService,
    private val securityService: SecurityService
) {

    companion object { private val log by ApiLogger() }

    private val appCustomerConverter = AppCustomerConverter()
    private val apiSessionConverter = ApiSessionConverter()

    fun login(loginRequestDTO: LoginRequestDTO): AuthAppCustomerResponseDTO {
        log.info("Login in customer with email [${loginRequestDTO.email}]")

        // Get user from database
        val appCustomer = appCustomerRepository.findAppCustomerByEmail(loginRequestDTO.email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(loginRequestDTO.email)
        // Authenticate user
        val apiSession = authService.authenticate(appCustomer, loginRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppCustomerResponseDTO(apiSessionConverter.apiSessionToApiSessionResponseDto(apiSession),
            appCustomerConverter.appCustomerToAppCustomerDto(appCustomer))
    }

    fun signUp(customerSignUpRequestDTO: CustomerSignUpRequestDTO): AuthAppCustomerResponseDTO {
        log.info("Signing up new customer with email [${customerSignUpRequestDTO.email}]")

        // Validate and encode password
        validatorService.validatePassword(customerSignUpRequestDTO.password)

        // Validate user information
        validatorService.validateEmail(customerSignUpRequestDTO.email)
        validatorService.validateEmailAlreadyUsed(customerSignUpRequestDTO.email)

        // Create new user
        val passwordHash = authService.encodePassword(customerSignUpRequestDTO.password)
        var newAppCustomer = appCustomerConverter.signUpDtoToAppCustomer(customerSignUpRequestDTO, passwordHash)
        newAppCustomer = appCustomerRepository.save(newAppCustomer)
        log.info("New customer was created with email [${newAppCustomer.email}] and id [${newAppCustomer.customerId}]")

        // Authenticate user
        val apiSession = authService.authenticate(newAppCustomer, customerSignUpRequestDTO.password)
        log.info("New admin authenticated with email [${newAppCustomer.email}] with token [${apiSession.token}]")

        // If no error was thrown, return response dto
        return AuthAppCustomerResponseDTO(apiSessionConverter.apiSessionToApiSessionResponseDto(apiSession),
            appCustomerConverter.appCustomerToAppCustomerDto(newAppCustomer))
    }

    fun forgotPassword(email: String) {
        log.info("Customer [$email] forgot password")

        // Get user from repository
        val appCustomer = appCustomerRepository.findAppCustomerByEmail(email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(email)

        // Create new api-session with RESET_PASSWORD permission
        val sessionToken = authService.generateNewSessionToken()
        val apiSession = apiSessionService.createAndSaveApiSession(appCustomer, sessionToken, listOf(Permission.RESET_PASSWORD), false)

        // Send to user's email session token to reset password
        emailService.send(AppEmails.ADMIN, email, "Forgot password",
            "To change password use session-token: " + apiSession.token)

        log.debug("Sent email to recover password to user [${appCustomer.role} / ${appCustomer.email}]")
    }

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        val currentAppCustomer = getCurrentCustomer()

        // Check if old-password matches current password
        if (!authService.passwordMatchesEncoded(resetPasswordRequest.oldPassword, currentAppCustomer.passwordHash)) {
            throw ApiExceptionModule.Auth.IncorrectPasswordException()
        }

        log.info("Changing admin password with email [${currentAppCustomer.email}]")
        currentAppCustomer.passwordHash = authService.encodePassword(resetPasswordRequest.newPassword)
        appCustomerRepository.save(currentAppCustomer)
        log.debug("Changed admin password with email [${currentAppCustomer.email}]")
    }

    fun forceResetPassword(newPassword: String) {

        // Double check if current session has ResetPassword permission - this was probably checked at controller level as well
        val currentPermissions = ApiSessionContext.getCurrentApiCallContext().apiSession!!.permissions
        if (!currentPermissions.contains(Permission.RESET_PASSWORD)) {
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(currentPermissions, ApiSessionContext.getCurrentApiCallContext().request.method)
        }

        // Change password for user of current session
        val currentAppAdmin = getCurrentCustomer()
        log.info("Changing admin password with email [${currentAppAdmin.email}]")
        currentAppAdmin.passwordHash = authService.encodePassword(newPassword)
        appCustomerRepository.save(currentAppAdmin)
        log.debug("Changed admin password with email [${currentAppAdmin.email}]")

        // Inactivate current session after successful password reset
        apiSessionService.inactivateCurrentSession()
    }

    fun getCurrentCustomer(): AppCustomer {
        return appUserService.getCurrentUserFromDb() as AppCustomer
    }

    fun getCurrentCustomerDto(): AppCustomerDTO {
        log.debug("Getting customer for current session")
        return appCustomerConverter.appCustomerToAppCustomerDto(getCurrentCustomer())
    }

    fun getCustomerDtoByEmail(email: String): AppCustomerDTO? {
        log.debug("Getting customer with email [$email]")
        val appCustomer = appCustomerRepository.findAppCustomerByEmail(email)
        return appCustomer?.let { appCustomerConverter.appCustomerToAppCustomerDto(it) }
    }

    /** Updatable fields are: name, email, phone, documentId, address, addressComplement
     * Any other field that has been modified will be ignored
     */
    fun updateCurrentCustomer(newAppCustomerDTO: AppCustomerDTO): AppCustomerDTO {
        val currentAppCustomer = appUserService.getCurrentUserFromDb() as AppCustomer

        // Verify if current user is trying to update some user other than himself
        if (currentAppCustomer.customerId != newAppCustomerDTO.customerId) {
            log.error("User ${currentAppCustomer.customerId} does not have enough privileges to update user with id ${newAppCustomerDTO.customerId}")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException("User ${currentAppCustomer.customerId} does not have enough privileges to update user with id ${newAppCustomerDTO.customerId}")
        }
        return updateCustomer(currentAppCustomer, newAppCustomerDTO)
    }

    fun updateCustomer(newAppCustomerDTO: AppCustomerDTO): AppCustomerDTO {
        // Verify if user of current session is has role of ADMIN
        securityService.verifyRoleForCurrentUser(UserRole.ADMIN)
        if (newAppCustomerDTO.customerId == null) throw ApiExceptionModule.General.NullPointer("customerDTO.customerId")
        log.info("Updating user with email [${newAppCustomerDTO.email}]")
        val currentAppCustomer = appCustomerRepository.getById(newAppCustomerDTO.customerId!!)
        return updateCustomer(currentAppCustomer, newAppCustomerDTO)
    }

    private fun updateCustomer(currentAppCustomer: AppCustomer, newAppCustomerDTO: AppCustomerDTO): AppCustomerDTO {
        log.debug("Updating app-customer [${newAppCustomerDTO.customerId}] information")

        // Updating
        currentAppCustomer.name = newAppCustomerDTO.name
        currentAppCustomer.email = newAppCustomerDTO.email
        currentAppCustomer.phone = newAppCustomerDTO.phone
        currentAppCustomer.documentId = newAppCustomerDTO.documentId
        currentAppCustomer.address = newAppCustomerDTO.address
        currentAppCustomer.addressComplement = newAppCustomerDTO.addressComplement
        // Persisting to database
        val newAppCustomer = appCustomerRepository.save(currentAppCustomer)
        log.info("AppCustomer with id [${newAppCustomer.customerId}] updated")
        // Returning DTO
        return appCustomerConverter.appCustomerToAppCustomerDto(newAppCustomer)

    }

    fun deleteCustomer(customerId: Long) {
        // Verify if user of current session is has role of ADMIN
        securityService.verifyRoleForCurrentUser(UserRole.ADMIN)
        log.debug("Deleting app-customer [$customerId]")
        appCustomerRepository.deleteByCustomerId(customerId)
    }

}