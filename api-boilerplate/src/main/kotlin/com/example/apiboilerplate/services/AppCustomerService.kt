package com.example.apiboilerplate.services

import com.example.apiboilerplate.converters.AppCustomerConverter
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.*
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.repositories.AppCustomerRepository
import com.example.apiboilerplate.services.base.AuthService
import org.springframework.stereotype.Service

@Service
class AppCustomerService(
    private val validatorService: ValidatorService,
    private val appCustomerRepository: AppCustomerRepository,
    private val authService: AuthService,
    private val appUserService: AppUserService
) {

    private val appCustomerConverter = AppCustomerConverter()

    // Controller related functions

    fun login(loginRequestDTO: LoginRequestDTO): AuthAppCustomerResponseDTO {
        // Get user from database
        val appCustomer = appCustomerRepository.getAppCustomerByEmail(loginRequestDTO.email)
            ?: throw ApiExceptionModule.User.UserNotFoundException(loginRequestDTO.email)
        // Authenticate user
        val apiSession = authService.authenticate(appCustomer, loginRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppCustomerResponseDTO(apiSession.token, appCustomerConverter.appCustomerToAppCustomerDto(appCustomer))
    }

    fun signUp(customerSignUpRequestDTO: CustomerSignUpRequestDTO): AuthAppCustomerResponseDTO {
        // Validate and encode password
        validatorService.validatePassword(customerSignUpRequestDTO.password)
        customerSignUpRequestDTO.password = authService.encodePassword(customerSignUpRequestDTO.password)

        // Validate user information
        validatorService.validateEmail(customerSignUpRequestDTO.email)
        validatorService.validateEmailAlreadyUsed(customerSignUpRequestDTO.email)

        // Create new user
        var newAppCustomer = appCustomerConverter.signUpDtoToAppCustomer(customerSignUpRequestDTO)
        newAppCustomer = appCustomerRepository.save(newAppCustomer)

        // Authenticate user
        val apiSession = authService.authenticate(newAppCustomer, customerSignUpRequestDTO.password)

        // If no error was thrown, return response dto
        return AuthAppCustomerResponseDTO(apiSession.token, appCustomerConverter.appCustomerToAppCustomerDto(newAppCustomer))
    }

    fun getCurrentCustomerDto(): AppCustomerDTO {
        return appCustomerConverter.appCustomerToAppCustomerDto(appUserService.getCurrentUser() as AppCustomer)
    }

    fun getCustomerDtoByEmail(email: String): AppCustomerDTO? {
        val appCustomer = appCustomerRepository.getAppCustomerByEmail(email)
        return appCustomer?.let { appCustomerConverter.appCustomerToAppCustomerDto(it) }
    }

}