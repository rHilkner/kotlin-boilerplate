package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

abstract class AppUserDTO(appUser: AppUser) {

    @JsonIgnore
    var userId: Long? = null

    @JsonProperty("email")
    lateinit var email: String

    @JsonProperty("name")
    lateinit var name: String

    @JsonProperty("profileImagePath")
    var profileImagePath: String? = null

    @JsonProperty("role")
    lateinit var role: UserRole

    init {
        ObjectUtil.copyProps(appUser, this)
    }

}