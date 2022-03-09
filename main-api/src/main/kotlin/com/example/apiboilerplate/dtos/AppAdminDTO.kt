package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

class AppAdminDTO(appAdmin: AppAdmin) {

    init {
        ObjectUtil.copyProps(appAdmin, this)
    }

    @JsonProperty("adminId")
    var adminId: Long? = null

    @JsonProperty("email")
    lateinit var email: String

    @JsonProperty("name")
    lateinit var name: String

    @JsonProperty("profileImagePath")
    lateinit var profileImagePath: String

    @JsonProperty("role")
    var role: UserRole = UserRole.ADMIN

}