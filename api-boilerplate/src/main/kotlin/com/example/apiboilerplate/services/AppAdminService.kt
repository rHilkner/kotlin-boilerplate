package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.converters.AppAdminConverter
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.services.base.AuthService
import com.example.apiboilerplate.services.base.SecurityService
import org.springframework.stereotype.Service

@Service
class AppAdminService(
    private val validatorService: ValidatorService,
    private val appAdminRepository: AppAdminRepository,
    private val authService: AuthService,
    private val appUserService: AppUserService,
    private val securityService: SecurityService
) {

    companion object { private val log by ApiLogger() }

    private val appAdminConverter = AppAdminConverter()

    // Controller related functions

    fun login(loginRequestDTO: LoginRequestDTO): AuthAppAdminResponseDTO {
        log.info("Login in admin with email [${loginRequestDTO.email}]")

        // Get user from database
        val appAdmin = appAdminRepository.getAppAdminByEmail(loginRequestDTO.email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(loginRequestDTO.email)
        // Authenticate user
        val apiSession = authService.authenticate(appAdmin, loginRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppAdminResponseDTO(apiSession.token, appAdminConverter.appAdminToAppAdminDto(appAdmin))
    }

    fun signUp(adminSignUpRequestDTO: AdminSignUpRequestDTO): AuthAppAdminResponseDTO {
        log.info("Signing up new admin with email [${adminSignUpRequestDTO.email}]")

        // Validate and encode password
        validatorService.validatePassword(adminSignUpRequestDTO.password)
        adminSignUpRequestDTO.password = authService.encodePassword(adminSignUpRequestDTO.password)

        // Validate user information
        validatorService.validateEmail(adminSignUpRequestDTO.email)
        validatorService.validateEmailAlreadyUsed(adminSignUpRequestDTO.email)

        // Create new user
        var newAppAdmin = appAdminConverter.signUpDtoToAppAdmin(adminSignUpRequestDTO)
        newAppAdmin = appAdminRepository.save(newAppAdmin)

        // Authenticate user
        val apiSession = authService.authenticate(newAppAdmin, adminSignUpRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppAdminResponseDTO(apiSession.token, appAdminConverter.appAdminToAppAdminDto(newAppAdmin))
    }

    fun getCurrentAdminDto(): AppAdminDTO {
        log.debug("Getting admin for current session")
        return appAdminConverter.appAdminToAppAdminDto(appUserService.getCurrentUserFromDb() as AppAdmin)
    }

    fun getAdminDtoByEmail(email: String): AppAdminDTO? {
        log.debug("Getting admin with email [$email]")
        val appAdmin = appAdminRepository.getAppAdminByEmail(email)
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
        val currentAppAdmin = appAdminRepository.getById(newAppAdminDTO.adminId)
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