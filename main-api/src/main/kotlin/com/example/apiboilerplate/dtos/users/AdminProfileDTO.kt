package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.models.user.AdminProfile
import com.fasterxml.jackson.annotation.JsonProperty

class AdminProfileDTO(adminProfile: AdminProfile): BaseUserProfileDTO(adminProfile) {

    @JsonProperty("dbWritePermission")
    var dbWritePermission: Boolean

    init {
        this.dbWritePermission = adminProfile.dbWritePermission
    }

}