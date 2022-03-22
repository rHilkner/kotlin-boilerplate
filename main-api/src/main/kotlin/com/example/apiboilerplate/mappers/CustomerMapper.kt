package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.CustomerProfile

class CustomerMapper {

    companion object { private val log by ApiLogger() }

    fun signUpDtoToCustomerProfile(signUpDto: SignUpRequestDTO, appUser: AppUser): CustomerProfile {
        return CustomerProfile(appUser)
    }

    fun customerProfileToCustomerProfileDto(customerProfile: CustomerProfile): CustomerProfileDTO {
        return CustomerProfileDTO(customerProfile)
    }

}