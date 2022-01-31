package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.exceptions.ApiException
import com.fasterxml.jackson.annotation.JsonProperty

data class ApiErrorDTO (

    @JsonProperty("httpStatusCode")
    var httpStatusCode: String,

    @JsonProperty("httpStatus")
    var httpStatus: String,

    @JsonProperty("errorMessage")
    var errorMessage: String

) {
    constructor(apiException: ApiException):
            this(apiException.httpStatus.value().toString(), apiException.httpStatus.name, apiException.errorMessage)
}