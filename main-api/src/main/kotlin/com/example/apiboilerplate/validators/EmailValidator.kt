package com.example.apiboilerplate.validators

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.user.AppUserService
import org.apache.commons.validator.routines.EmailValidator
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 IMPORTANT: All public functions must throw an exception
 */

@Component
class EmailValidator(
    @Lazy
    private val appUserService: AppUserService
) {

    fun emailFormat(email: String) {
        if (!EmailValidator.getInstance().isValid(email)) {
            throw ApiExceptionModule.Auth.InvalidEmailFormatException(email)
        }
    }

    fun emailNotAlreadyUsed(email: String, userRole: UserRole) {
        // Try to find user with email
        val appUser = appUserService.getByEmail(email, userRole)
        // If user found, throw exception
        if (appUser != null) {
            throw ApiExceptionModule.Auth.EmailAlreadyUsedException(email)
        }
    }

}