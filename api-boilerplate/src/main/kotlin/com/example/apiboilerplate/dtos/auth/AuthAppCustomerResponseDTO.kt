package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppCustomerDTO

data class AuthAppCustomerResponseDTO(
    val apiSession: ApiSessionResponseDTO,
    val appCustomer: AppCustomerDTO
)