package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.converters.AppUserConverter
import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.auth.AuthResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.repositories.AppUserRepository
import com.example.apiboilerplate.services.base.AuthService
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val validatorService: ValidatorService,
    private val appUserRepository: AppUserRepository,
    private val authService: AuthService
) {

    private val appUserConverter = AppUserConverter()

    // Controller related functions

    fun login(loginRequestDTO: LoginRequestDTO): AuthResponseDTO {
        // Get user from database
        val appUser = appUserRepository.getAppUserByEmail(loginRequestDTO.email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(loginRequestDTO.email)
        // Authenticate user
        val apiSession = authService.authenticate(appUser, loginRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthResponseDTO(apiSession.token, appUser)
    }

    fun signUp(signUpRequestDTO: SignUpRequestDTO): AuthResponseDTO {
        // Validate and encode password
        validatorService.validatePassword(signUpRequestDTO.password)
        signUpRequestDTO.password = authService.encodePassword(signUpRequestDTO.password)

        // Validate user information
        validatorService.validateEmail(signUpRequestDTO.email)
        validatorService.validateEmailAlreadyUsed(signUpRequestDTO.email)

        // Create new user
        var newAppUser = appUserConverter.signUpDtoToAppUser(signUpRequestDTO, UserRole.CUSTOMER)
        newAppUser = appUserRepository.save(newAppUser)

        // Authenticate user
        val apiSession = authService.authenticate(newAppUser, signUpRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthResponseDTO(apiSession.token, newAppUser)
    }

    fun getUserDtoByEmail(email: String): AppUserDTO? {
        val appUser = appUserRepository.getAppUserByEmail(email)
        return appUser?.let { appUserConverter.appUserToAppUserDto(it) }
    }

    fun getCurrentUserDto(): AppUserDTO? {
        val appUser = getCurrentUser()
        return appUser?.let { appUserConverter.appUserToAppUserDto(it) }
    }

    fun getCurrentUser(): AppUser? {
        val userId = ApiCallContext.getCurrentApiCallContext().currentUserId
        return userId?.let { appUserRepository.getById(it) }
    }

}