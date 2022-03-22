package com.example.apiboilerplate.converters

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpAppCustomerRequestDTO
import com.example.apiboilerplate.dtos.users.CustomerProfileDTO
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.CustomerProfile

class CustomerConverter {

    companion object { private val log by ApiLogger() }

    fun signUpDtoToCustomerProfile(signUpDto: SignUpAppCustomerRequestDTO, appUser: AppUser): CustomerProfile {
        return CustomerProfile(signUpDto, appUser)
    }

    fun customerProfileToCustomerProfileDto(customerProfile: CustomerProfile): CustomerProfileDTO {
        return CustomerProfileDTO(customerProfile)
    }

}