package com.example.apiboilerplate.exceptions

import org.springframework.http.HttpStatus
import java.util.*


abstract class ApiException(
    var httpStatus: HttpStatus,
    var errorMessage: String,
    var debugMessage: String = errorMessage
) : Exception() {
    var timestamp: Date = Date()
}
