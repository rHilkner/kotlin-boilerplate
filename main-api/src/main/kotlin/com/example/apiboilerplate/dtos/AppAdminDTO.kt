package com.example.apiboilerplate.dtos

import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppAdmin
import com.example.apiboilerplate.utils.ObjectUtil
import com.fasterxml.jackson.annotation.JsonProperty

class AppAdminDTO(appAdmin: AppAdmin): AppUserDTO(appAdmin, UserRole.ADMIN) {

    init {
        ObjectUtil.copyProps(appAdmin, this)
    }

    @JsonProperty("adminId")
    var adminId: Long? = super.userId

}