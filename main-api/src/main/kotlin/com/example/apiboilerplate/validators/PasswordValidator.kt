package com.example.apiboilerplate.validators

import com.example.apiboilerplate.exceptions.ApiExceptionModule
import org.springframework.stereotype.Component

/**
 IMPORTANT: All public functions must throw an exception
 */

@Component
class PasswordValidator {

    fun passwordFormat(password: String) {
        if (password.length < 8) {
            throw ApiExceptionModule.Auth.InvalidPasswordException("Password must have at least 8 characters", "Password has ${password.length} characters")
        }
    }

}