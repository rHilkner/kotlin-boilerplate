package com.example.apiboilerplate.dtos.users

import com.fasterxml.jackson.annotation.JsonProperty

abstract class FullUserDTO(appUser: AppUserDTO, userProfile: UserProfileDTO) {

    @JsonProperty("appUser")
    val appUser: AppUserDTO
    @JsonProperty("userProfile")
    val userProfile: UserProfileDTO

    init {
        this.appUser = appUser
        this.userProfile = userProfile
    }

}