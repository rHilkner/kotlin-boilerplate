package com.example.apiboilerplate.exceptions

import com.example.apiboilerplate.enums.Permission
import com.example.apiboilerplate.enums.UserRole
import org.springframework.http.HttpStatus

class ApiExceptionModule {

    class General {
        class UnexpectedException(errorMessage: String, debugMessage: String = errorMessage)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, debugMessage)
    }

    class Auth {
        class UnauthenticatedCallException
            : ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized session for method", "")
        class NotEnoughPrivilegesException: ApiException {
            constructor(sessionRole: UserRole, method: String) : super(HttpStatus.FORBIDDEN, "Not enough privileges for method: Insufficient role", "Session role [${sessionRole.name}] not authorized for method $method")
            constructor(permissionList: List<Permission>, method: String) : super(HttpStatus.FORBIDDEN, "Not enough privileges for method: Insufficient permission", "Session permissions [${permissionList.toList()}] not authorized for method $method")
            constructor(errorMessage: String, debugMessage: String = errorMessage): super(HttpStatus.FORBIDDEN, errorMessage, debugMessage)
        }
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
        class UserNotFoundException(email: String)
            : ApiException(HttpStatus.NO_CONTENT, "User $email not found")
    }

    class Email {
        class FailedToSendEmail(email: String)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email to $email")
    }

}