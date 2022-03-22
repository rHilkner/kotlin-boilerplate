package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class AppUserDTO(appUser: AppUser) {

    @JsonIgnore
    var userId: Long? = null

    @JsonProperty("userUuid")
    var userUuid: UUID

    @JsonProperty("email")
    var email: String

    @JsonProperty("name")
    var name: String

    @JsonProperty("role")
    var role: UserRole

    init {
        this.userId = appUser.userId
        this.userUuid = appUser.userUuid
        this.email = appUser.email
        this.name = appUser.name
        this.role = appUser.role
    }

}