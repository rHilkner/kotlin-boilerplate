package com.example.apiboilerplate.converters

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpAdminRequestDTO
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.models.AdminProfile
import com.example.apiboilerplate.models.AppUser

class AdminConverter {

    companion object { private val log by ApiLogger() }

    fun signUpDtoToAdminProfile(signUpDto: SignUpAdminRequestDTO, appUser: AppUser): AdminProfile {
        return AdminProfile(appUser, signUpDto)
    }

    fun adminProfileToAdminProfileDto(adminProfile: AdminProfile): AdminProfileDTO {
        return AdminProfileDTO(adminProfile)
    }

}