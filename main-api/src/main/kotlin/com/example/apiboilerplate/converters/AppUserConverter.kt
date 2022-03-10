package com.example.apiboilerplate.converters

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppAdminRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppCustomerRequestDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.models.AppUser

class AppUserConverter {

    companion object { private val log by ApiLogger() }

    private val appAdminConverter = AppAdminConverter()
    private val appCustomerConverter = AppCustomerConverter()

    fun appUserToAppUserDto(appUser: AppUser): AppUserDTO {
        log.debug("Converting AppUser to AppUserDTO")
        return when (appUser.role) {
            UserRole.ADMIN -> AppAdminDTO(appUser as AppAdmin)
            UserRole.CUSTOMER -> AppCustomerDTO(appUser as AppCustomer)
        }
    }

    fun signUpDtoToAppUser(signUpRequestDTO: SignUpRequestDTO, passwordHash: String): AppUser {
        log.debug("Converting SignUpRequestDTO to AppUser")
        val userRole: UserRole = when (signUpRequestDTO) {
            is SignUpAppAdminRequestDTO -> UserRole.ADMIN
            is SignUpAppCustomerRequestDTO -> UserRole.CUSTOMER
            else -> throw ApiExceptionModule.General.UnexpectedException(
                "SignUpRequestDTO is not SignUpAppAdminRequestDTO or SignUpAppCustomerRequestDTO")
        }
        return when (userRole) {
            UserRole.ADMIN ->
                appAdminConverter.signUpDtoToAppAdmin(signUpRequestDTO as SignUpAppAdminRequestDTO, passwordHash)
            UserRole.CUSTOMER ->
                appCustomerConverter.signUpDtoToAppCustomer(signUpRequestDTO as SignUpAppCustomerRequestDTO, passwordHash)
        }
    }

}