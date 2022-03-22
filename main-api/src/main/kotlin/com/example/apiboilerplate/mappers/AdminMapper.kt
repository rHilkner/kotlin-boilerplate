package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.models.user.AdminProfile
import com.example.apiboilerplate.models.user.AppUser

class AdminMapper {

    companion object { private val log by ApiLogger() }

    fun signUpDtoToAdminProfile(signUpDto: SignUpRequestDTO, appUser: AppUser): AdminProfile {
        return AdminProfile(appUser)
    }

    fun adminProfileToAdminProfileDto(adminProfile: AdminProfile): AdminProfileDTO {
        return AdminProfileDTO(adminProfile)
    }

}