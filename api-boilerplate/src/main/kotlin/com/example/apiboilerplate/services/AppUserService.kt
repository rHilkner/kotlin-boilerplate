package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.models.AppCustomer
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AppUserService(
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository
) {

    companion object { private val log by ApiLogger() }

    fun getCurrentUserFromDb(cached: Boolean = false): AppUser? {

        log.debug("Selecting current session's user from database")

        // Check if there is a current user
        val userId = ApiCallContext.getCurrentApiCallContext().currentUserId ?: return null
        val userRole = ApiCallContext.getCurrentApiCallContext().currentUserRole
            ?: throw ApiExceptionModule.General.UnexpectedException("User role not found")

        // Get user from database
        val appUser = when (userRole) {
            UserRole.ADMIN -> appAdminRepository.getById(userId)
            UserRole.CUSTOMER -> appCustomerRepository.getById(userId)
        }

        // Update user
        val appUserUpdated = updateLastActivityDt(appUser)
        ApiCallContext.getCurrentApiCallContext().currentUser = appUserUpdated

        log.debug("Current session's user with id [${appUserUpdated.userId}] selected from database")

        return appUser
    }

    fun updateLastActivityDt(appUser: AppUser): AppUser {
        log.debug("Updating lastActivityDt of user [${appUser.userId}]")
        appUser.lastAccessDt = Date()
        val updatedAppUser = when (appUser.role) {
            UserRole.ADMIN -> appAdminRepository.save(appUser as AppAdmin)
            UserRole.CUSTOMER -> appCustomerRepository.save(appUser as AppCustomer)
        }
        log.debug("lastActivityDt updated for user [${appUser.userId}]")
        return updatedAppUser
    }

}