package com.example.apiboilerplate.converters

import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.CustomerSignUpRequestDTO
import com.example.apiboilerplate.models.AppCustomer

class AppCustomerConverter {

    fun signUpDtoToAppCustomer(customerSignUpRequestDTO: CustomerSignUpRequestDTO): AppCustomer {
        return AppCustomer(customerSignUpRequestDTO)
    }

    fun appCustomerToAppCustomerDto(appCustomer: AppCustomer): AppCustomerDTO {
        return AppCustomerDTO(appCustomer)
    }

}