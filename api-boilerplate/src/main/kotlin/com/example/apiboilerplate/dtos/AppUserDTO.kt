package com.example.apiboilerplate.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class AppUserDTO(

    @JsonProperty("userId")
    val userId: Long?,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("name")
    val name: String

)