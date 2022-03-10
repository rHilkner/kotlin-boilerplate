package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

abstract class AppUserDTO(appUser: AppUser, userRole: UserRole) {

    var userId: Long?

    @JsonProperty("email")
    lateinit var email: String

    @JsonProperty("name")
    lateinit var name: String

    @JsonProperty("profileImagePath")
    lateinit var profileImagePath: String

    @JsonProperty("role")
    var role: UserRole

    init {
        this.userId = appUser.userId
        this.role = userRole
        ObjectUtil.copyProps(appUser, this)
    }

}