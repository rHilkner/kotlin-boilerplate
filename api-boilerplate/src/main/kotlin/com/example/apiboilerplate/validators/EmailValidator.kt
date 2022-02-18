package com.example.apiboilerplate.validators

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.stereotype.Component

/**
 IMPORTANT: All public functions must throw an exception
 */

@Component
class EmailValidator(
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository
) {

    fun emailFormat(email: String) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw ApiExceptionModule.Auth.InvalidEmailFormatException(email)
        }
    }

    fun emailNotAlreadyUsed(email: String, userRole: UserRole) {
        // Try to find user with email
        val appUser = when (userRole) {
            UserRole.CUSTOMER -> appCustomerRepository.findAppCustomerByEmail(email)
            UserRole.ADMIN -> appAdminRepository.findAppAdminByEmail(email)
        }
        // If user found, throw exception
        if (appUser != null) {
            throw ApiExceptionModule.Auth.EmailAlreadyUsedException(email)
        }
    }

}