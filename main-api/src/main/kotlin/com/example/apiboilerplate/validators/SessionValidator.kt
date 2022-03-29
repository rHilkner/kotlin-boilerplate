package com.example.apiboilerplate.validators

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.user.AppUserService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class SessionValidator(
    @Lazy
    private val appUserService: AppUserService
) {

    fun validateCurrentUserRole(expectedRole: UserRole) {
        val currentUser = appUserService.getCurrentUserOrThrow()
        if (currentUser.role != expectedRole) {
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(currentUser.role, expectedRole)
        }
    }

}