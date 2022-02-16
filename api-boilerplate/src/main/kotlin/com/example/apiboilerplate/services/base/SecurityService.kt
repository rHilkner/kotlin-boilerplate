package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiSessionContext
import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import org.springframework.stereotype.Service

@Service
class SecurityService {

    companion object { private val log by ApiLogger() }

    fun verifyRoleForCurrentUser(roleExpected: UserRole) {
        val currentUserRole = ApiSessionContext.getCurrentApiCallContext().currentUserRole
        if (currentUserRole != roleExpected) {
            log.error("Role verification failed")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException("Role expected: $roleExpected; role provided: $currentUserRole")
        }
    }

}