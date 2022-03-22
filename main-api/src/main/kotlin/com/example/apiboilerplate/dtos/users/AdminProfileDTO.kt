package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.models.AdminProfile
import com.fasterxml.jackson.annotation.JsonProperty

class AdminProfileDTO(adminProfile: AdminProfile): UserProfileDTO() {

    @JsonProperty("dbWritePermission")
    var dbWritePermission: Boolean

    init {
        this.dbWritePermission = adminProfile.dbWritePermission
    }

}