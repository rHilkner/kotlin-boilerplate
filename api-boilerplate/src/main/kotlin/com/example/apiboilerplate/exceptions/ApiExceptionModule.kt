package com.example.apiboilerplate.exceptions

import org.springframework.http.HttpStatus

class ApiExceptionModule {

    class General {
        class UnexpectedException(errorMessage: String, debugMessage: String = errorMessage)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, debugMessage)
    }

    class Auth {
        class UnauthenticatedCallException
            : ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized session for method", "")
        class NotEnoughPrivilegesException(sessionRoles: String, var method: String)
            : ApiException(HttpStatus.FORBIDDEN, "Not enough privileges for method", "Session roles $sessionRoles not authorized for method $method")
        class IncorrectPasswordException()
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

}