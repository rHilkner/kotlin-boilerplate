package com.example.apiboilerplate.exceptions

import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import org.springframework.http.HttpStatus
import java.util.*

class ApiExceptionModule {

    class General {
        class UnexpectedException(errorMessage: String, debugMessage: String = errorMessage)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, debugMessage)
        class NullPointer(errorMessage: String, debugMessage: String = errorMessage)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, debugMessage)
        class BadRequestException(errorMessage: String, debugMessage: String = errorMessage)
            : ApiException(HttpStatus.BAD_REQUEST, errorMessage, debugMessage)
        class NotImplementedException(errorMessage: String, debugMessage: String = errorMessage)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, debugMessage)
    }

    class Auth {
        class UnauthenticatedCallException
            : ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized session for method", "")
        class NotEnoughPrivilegesException: ApiException {
            constructor(currentUserRole: UserRole?, expectedUserRole: UserRole) : super(HttpStatus.FORBIDDEN, "Not enough privileges: Session's user-role is [$currentUserRole] and expected role is [$expectedUserRole]")
            constructor(userRole: UserRole?, endpoint: String) : super(HttpStatus.FORBIDDEN, "Not enough privileges (insufficient role) at endpoint [$endpoint]", "Session role [${userRole?.name}] not authorized for endpoint [$endpoint]")
            constructor(permissionList: List<Permission>, method: String) : super(HttpStatus.FORBIDDEN, "Not enough privileges for method: Insufficient permission", "Session permissions [${permissionList.toList()}] not authorized for method $method")
            constructor(errorMessage: String, debugMessage: String = errorMessage): super(HttpStatus.FORBIDDEN, errorMessage, debugMessage)
        }
        class InvalidSessionToken(token: String)
            : ApiException(HttpStatus.FORBIDDEN, "Invalid session token [$token]")
        class ExpiredSessionToken(token: String)
            : ApiException(HttpStatus.FORBIDDEN, "Expired session token [$token]")
        class IncorrectPasswordException
            : ApiException(HttpStatus.UNAUTHORIZED, "Incorrect password")

        class InvalidEmailFormatException(email: String)
            : ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Email $email not in correct format")
        class EmailAlreadyUsedException(email: String)
            : ApiException(HttpStatus.CONFLICT, "Email $email already registered")
        class InvalidPasswordException(errorMessage: String, debugMessage: String?)
            : ApiException(HttpStatus.FORBIDDEN, errorMessage, debugMessage ?: errorMessage)
    }

    class User {
        class UserNotFoundException: ApiException {
            constructor(uuid: UUID) : super(HttpStatus.NO_CONTENT, "User $uuid not found")
            constructor(id: Long) : super(HttpStatus.NO_CONTENT, "User $id not found")
            constructor(email: String) : super(HttpStatus.NO_CONTENT, "User $email not found")
        }
    }

    class Email {
        class FailedToSendEmail(email: String)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email to $email")
    }

}