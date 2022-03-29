package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.IAppDTO
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class ForceResetPasswordRequest(

    @JsonProperty("newPassword")
    @NotNull(message = "Password should not be null")
    var newPassword: String

): IAppDTO