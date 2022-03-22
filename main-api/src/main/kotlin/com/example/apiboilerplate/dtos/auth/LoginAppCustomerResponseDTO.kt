package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.CustomerDTO

class LoginAppCustomerResponseDTO(
    customer: CustomerDTO,
    apiSession: ApiSessionResponseDTO
) : LoginResponseDTO(customer, apiSession)