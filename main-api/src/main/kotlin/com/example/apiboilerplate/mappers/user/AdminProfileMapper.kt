package com.example.apiboilerplate.mappers.user

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginAdminResponseDTO
import com.example.apiboilerplate.dtos.users.AdminProfileDTO
import com.example.apiboilerplate.models.user.AdminProfile
import com.example.apiboilerplate.models.user.AppUser

class AdminProfileMapper: UserProfileMapper<AdminProfile, AdminProfileDTO, LoginAdminResponseDTO>() {

    override fun createUserProfile(appUser: AppUser): AdminProfile {
        return AdminProfile(appUser)
    }

    override fun toDto(model: AdminProfile): AdminProfileDTO {
        return AdminProfileDTO(model)
    }

    override fun buildLoginResponse(userProfile: AdminProfileDTO, apiSession: ApiSessionResponseDTO): LoginAdminResponseDTO {
        return LoginAdminResponseDTO(userProfile, apiSession)
    }

}