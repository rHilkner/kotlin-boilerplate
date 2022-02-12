package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

class AppAdminDTO {

    constructor(appAdmin: AppAdmin, role: UserRole) {
        ObjectUtil.copyProps(this, appAdmin)
        this.role = role
    }

    @JsonProperty("adminId")
    var adminId: Long = 0

    @JsonProperty("email")
    lateinit var email: String

    @JsonProperty("name")
    lateinit var name: String

    @JsonProperty("role")
    lateinit var role: UserRole

}