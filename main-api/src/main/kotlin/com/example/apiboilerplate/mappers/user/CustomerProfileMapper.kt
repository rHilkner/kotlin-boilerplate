package com.example.apiboilerplate.mappers.user

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.dtos.auth.LoginCustomerResponseDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.CustomerProfile

class CustomerProfileMapper: UserProfileMapper<CustomerProfile, CustomerProfileDTO, LoginCustomerResponseDTO>() {

    override fun createUserProfile(appUser: AppUser): CustomerProfile {
        return CustomerProfile(appUser)
    }

    override fun toDto(model: CustomerProfile): CustomerProfileDTO {
        return CustomerProfileDTO(model)
    }

    override fun buildLoginResponse(userProfile: CustomerProfileDTO, apiSession: ApiSessionResponseDTO): LoginCustomerResponseDTO {
        return LoginCustomerResponseDTO(userProfile, apiSession)
    }

}