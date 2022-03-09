package com.example.apiboilerplate.validators

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.services.AppUserService
import org.springframework.stereotype.Component

@Component
class StoragePermissionsValidator(
    @org.springframework.context.annotation.Lazy
    private val appUserService: AppUserService
) {

    companion object { private val log by ApiLogger() }

    // Anyone can read from public directory; only Admins and the session user can read from private directory
    fun checkCurrentUserReadPermissions(path: String) {
        val currentUser = appUserService.getCurrentUserOrThrow()
        if (path.contains("/public/") || currentUser.role == UserRole.ADMIN) {
            return
        }

        if (!path.contains("/customer/${currentUser.userId}/")) {
            log.error("User [${currentUser.userId}] don't have privileges to read from path [$path]")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(
                "User [${currentUser.userId}] don't have privileges to read from path [$path]")
        }
    }

    // Only Admins and the session user can write in a user's folder (private or public)
    fun checkCurrentUserWritePermissions(path: String) {
        val currentUser = appUserService.getCurrentUserOrThrow()
        if (currentUser.role != UserRole.ADMIN) {
            return
        }

        if (!path.contains("/customer/${currentUser.userId}/")) {
            log.error("User [${currentUser.userId}] don't have privileges to write to path [$path]")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException(
                "User [${currentUser.userId}] don't have privileges to write to path [$path]")
        }
    }

}