package com.example.apiboilerplate.services

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.stereotype.Service

/**
 IMPORTANT: All public functions must throw an exception
 */

@Service
class ValidatorService(
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository
) {

    fun validateEmail(email: String) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw ApiExceptionModule.Auth.InvalidEmailFormatException(email)
        }
    }

    fun validateEmailAlreadyUsed(email: String, userRole: UserRole) {
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

    fun validatePassword(password: String) {
        if (password.length < 8) {
            throw ApiExceptionModule.Auth.InvalidPasswordException("Password must have at least 8 characters", "Password has ${password.length} characters")
        }
    }

}