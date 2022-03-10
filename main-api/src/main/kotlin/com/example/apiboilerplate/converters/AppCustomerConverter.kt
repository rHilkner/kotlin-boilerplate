package com.example.apiboilerplate.converters

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.AppCustomerDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppCustomerRequestDTO
import com.example.apiboilerplate.models.AppCustomer

class AppCustomerConverter {

    companion object { private val log by ApiLogger() }

    fun appCustomerToAppCustomerDto(appCustomer: AppCustomer): AppCustomerDTO {
        log.debug("Converting AppCustomer to AppCustomerDTO")
        return AppCustomerDTO(appCustomer)
    }

    fun signUpDtoToAppCustomer(signUpAppCustomerRequestDTO: SignUpAppCustomerRequestDTO, passwordHash: String): AppCustomer {
        log.debug("Converting SignUpAppCustomerRequestDTO to AppCustomer")
        return AppCustomer(signUpAppCustomerRequestDTO, passwordHash)
    }

}