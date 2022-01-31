package com.example.apiboilerplate.exceptions

import org.springframework.http.HttpStatus

class ApiExceptionModule {

    class General {
        class UnexpectedException(ex: Exception)
            : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex.stackTraceToString())
    }

    class Auth {
        class EmailInvalidFormatException(var email: String)
            : ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "Email $email not in correct format")
        class EmailAlreadyUsedException(var email: String)
            : ApiException(HttpStatus.CONFLICT, "Email $email already registered")
        class InvalidPasswordException(errorMessage: String, debugMessage: String?)
            : ApiException(HttpStatus.FORBIDDEN, errorMessage, debugMessage ?: errorMessage)
    }

    class User {
        class UserNotFoundException(var email: String)
            : ApiException(HttpStatus.NO_CONTENT, "User $email not found")
    }

}