package com.example.apiboilerplate.services

import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.repositories.AppUserRepository
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.stereotype.Service

/**
 IMPORTANT: All public functions must throw an exception
 */

@Service
class ValidatorService(
    private val appUserDAO: AppUserRepository,
) {

    fun validateEmail(email: String) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw ApiExceptionModule.Auth.InvalidEmailFormatException(email)
        }
    }

    fun validateEmailAlreadyUsed(email: String) {
        if (appUserDAO.getAppUserByEmail(email) != null) {
            throw ApiExceptionModule.Auth.EmailAlreadyUsedException(email)
        }
    }

    fun validatePassword(password: String) {
        if (password.length < 8) {
            throw ApiExceptionModule.Auth.InvalidPasswordException("Password must have at least 8 characters", "Password has ${password.length} characters")
        }
    }

}