package com.example.apiboilerplate.mappers.user

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginPartnerResponseDTO
import com.example.apiboilerplate.dtos.users.PartnerProfileDTO
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.PartnerProfile

class PartnerProfileMapper: UserProfileMapper<PartnerProfile, PartnerProfileDTO, LoginPartnerResponseDTO>() {

    override fun createUserProfile(appUser: AppUser): PartnerProfile {
        return PartnerProfile(appUser)
    }

    override fun toDto(model: PartnerProfile): PartnerProfileDTO {
        return PartnerProfileDTO(model)
    }

    override fun buildLoginResponse(userProfile: PartnerProfileDTO, apiSession: ApiSessionResponseDTO): LoginPartnerResponseDTO {
        return LoginPartnerResponseDTO(userProfile, apiSession)
    }

}