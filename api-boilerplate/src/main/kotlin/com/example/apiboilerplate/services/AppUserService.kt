package com.example.apiboilerplate.services

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.repositories.AppAdminRepository
import com.example.apiboilerplate.repositories.AppCustomerRepository
import org.springframework.stereotype.Service

@Service
class AppUserService(
    private val appAdminRepository: AppAdminRepository,
    private val appCustomerRepository: AppCustomerRepository
) {

    fun getCurrentUser(): AppUser? {
        // If current user already instantiated, return it
        if (ApiCallContext.getCurrentApiCallContext().currentUser != null) {
            return ApiCallContext.getCurrentApiCallContext().currentUser
        }

        // Check if there is a current user
        val userId = ApiCallContext.getCurrentApiCallContext().currentUserId ?: return null
        val userRole = ApiCallContext.getCurrentApiCallContext().currentUserRole

        // Get user from database
        val appUser = when (userRole) {
            UserRole.CUSTOMER -> appCustomerRepository.getById(userId)
            UserRole.ADMIN -> appAdminRepository.getById(userId)
            else -> throw ApiExceptionModule.General.UnexpectedException("User role different than CUSTOMER and ADMIN")
        }

        updateLastActivityDt(appUser)
        return appUser
    }

    fun updateLastActivityDt(appUser: AppUser?) {

    }

}