package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.fasterxml.jackson.annotation.JsonProperty

data class AppUserDTO(

    @JsonProperty("userId")
    val userId: Long?,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("role")
    val role: UserRole

)