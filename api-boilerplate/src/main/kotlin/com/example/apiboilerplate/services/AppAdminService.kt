package com.example.apiboilerplate.services

import com.example.apiboilerplate.converters.AppAdminConverter
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthAppAdminResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.services.base.AuthService
import org.springframework.stereotype.Service

@Service
class AppAdminService(
    private val validatorService: ValidatorService,
    private val appAdminRepository: AppAdminRepository,
    private val authService: AuthService,
    private val appUserService: AppUserService
) {

    private val appAdminConverter = AppAdminConverter()

    // Controller related functions

    fun login(loginRequestDTO: LoginRequestDTO): AuthAppAdminResponseDTO {
        // Get user from database
        val appAdmin = appAdminRepository.getAppAdminByEmail(loginRequestDTO.email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(loginRequestDTO.email)
        // Authenticate user
        val apiSession = authService.authenticate(appAdmin, loginRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppAdminResponseDTO(apiSession.token, appAdminConverter.appAdminToAppAdminDto(appAdmin))
    }

    fun signUp(adminSignUpRequestDTO: AdminSignUpRequestDTO): AuthAppAdminResponseDTO {
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
        return appAdminConverter.appAdminToAppAdminDto(appUserService.getCurrentUser() as AppAdmin)
    }

    fun getAdminDtoByEmail(email: String): AppAdminDTO? {
        val appAdmin = appAdminRepository.getAppAdminByEmail(email)
        return appAdmin?.let { appAdminConverter.appAdminToAppAdminDto(it) }
    }

}