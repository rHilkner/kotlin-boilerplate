package com.example.apiboilerplate.dtos.auth

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class ResetPasswordRequest(

    @JsonProperty("oldPassword")
    @NotNull(message = "Password should not be null")
    val oldPassword: String,

    @JsonProperty("newPassword")
    @NotNull(message = "Password should not be null")
    var newPassword: String

)