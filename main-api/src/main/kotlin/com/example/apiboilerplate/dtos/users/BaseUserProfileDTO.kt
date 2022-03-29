package com.example.apiboilerplate.dtos.users

import com.example.apiboilerplate.dtos.IAppDTO
import com.example.apiboilerplate.models.user.BaseUserProfile
import com.fasterxml.jackson.annotation.JsonProperty

abstract class BaseUserProfileDTO(userProfile: BaseUserProfile): IAppDTO {

    @JsonProperty("appUser")
    val appUser: AppUserDTO

    init {
        appUser = AppUserDTO(userProfile.appUser)
    }

}