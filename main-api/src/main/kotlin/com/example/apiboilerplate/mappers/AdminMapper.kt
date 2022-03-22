package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpAdminRequestDTO
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.models.user.AdminProfile
import com.example.apiboilerplate.models.user.AppUser

class AdminMapper {

    companion object { private val log by ApiLogger() }

    fun signUpDtoToAdminProfile(signUpDto: SignUpAdminRequestDTO, appUser: AppUser): AdminProfile {
        return AdminProfile(appUser, signUpDto)
    }

    fun adminProfileToAdminProfileDto(adminProfile: AdminProfile): AdminProfileDTO {
        return AdminProfileDTO(adminProfile)
    }

}