package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.CustomerProfileDTO

class LoginCustomerResponseDTO(
    customer: CustomerProfileDTO,
    apiSession: ApiSessionResponseDTO
) : BaseLoginResponseDTO(customer, apiSession)