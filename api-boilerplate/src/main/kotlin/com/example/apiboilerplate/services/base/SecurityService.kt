package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.ApiCallContext
import com.example.apiboilerplate.base.logger.log
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.exceptions.ApiExceptionModule
import org.springframework.stereotype.Service

@Service
class SecurityService {

    fun verifyRoleForCurrentUser(roleExpected: UserRole) {
        val currentUserRole = ApiCallContext.getCurrentApiCallContext().currentUserRole
        if (currentUserRole != roleExpected) {
            log.error("Role verification failed")
            throw ApiExceptionModule.Auth.NotEnoughPrivilegesException("Role expected: $roleExpected; role provided: $currentUserRole")
        }
    }

}