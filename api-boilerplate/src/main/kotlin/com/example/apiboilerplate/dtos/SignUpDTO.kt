package com.example.apiboilerplate.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class SignUpDTO(

    @JsonProperty("email")
    @NotNull(message = "Email should not be null")
    val email: String,

    @JsonProperty("name")
    @NotNull(message = "Name should not be null")
    val name: String,

    @JsonProperty("password")
    @NotNull(message = "Password should not be null")
    var password: String

)