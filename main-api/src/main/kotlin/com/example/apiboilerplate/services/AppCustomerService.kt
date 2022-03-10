package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.LoginAppCustomerResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppCustomerRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppCustomer
import org.springframework.stereotype.Service

@Service
class AppCustomerService(
    private val appUserService: AppUserService
) {

    companion object { private val log by ApiLogger() }

    fun login(loginRequestDTO: LoginRequestDTO): LoginAppCustomerResponseDTO {
        return appUserService.login(loginRequestDTO, UserRole.CUSTOMER) as LoginAppCustomerResponseDTO
    }

    fun signUp(signUpAppCustomerRequestDTO: SignUpAppCustomerRequestDTO): LoginAppCustomerResponseDTO {
        return appUserService.signUp(signUpAppCustomerRequestDTO, UserRole.CUSTOMER) as LoginAppCustomerResponseDTO
    }

    fun getCurrentCustomer(): AppCustomer {
        return appUserService.getCurrentUser() as AppCustomer
    }

    fun getCurrentCustomerDto(): AppCustomerDTO {
        return appUserService.getCurrentUserDto() as AppCustomerDTO
    }

    fun getCustomerDtoByEmail(email: String): AppCustomerDTO? {
        return appUserService.getUserDtoByEmail(email, UserRole.CUSTOMER) as AppCustomerDTO?
    }

    /** Updatable fields are: name, email, phone, documentId, address, addressComplement
     * Any other field that has been modified will be ignored
     */
    fun updateCurrentCustomer(newAppCustomerDTO: AppCustomerDTO): AppCustomerDTO {
        return appUserService.updateCurrentUser(newAppCustomerDTO) as AppCustomerDTO
    }

    fun updateCustomer(newAppCustomerDTO: AppCustomerDTO): AppCustomerDTO {
        return appUserService.updateUser(newAppCustomerDTO) as AppCustomerDTO
    }

    fun deleteCustomer(customerId: Long) {
        appUserService.deleteUser(customerId, UserRole.CUSTOMER)
    }

}