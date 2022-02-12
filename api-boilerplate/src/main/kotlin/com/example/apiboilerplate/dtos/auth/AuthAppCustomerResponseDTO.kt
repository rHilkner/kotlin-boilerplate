package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppCustomerDTO

data class AuthAppCustomerResponseDTO(
    val apiSessionToken: String,
    val appCustomer: AppCustomerDTO
)