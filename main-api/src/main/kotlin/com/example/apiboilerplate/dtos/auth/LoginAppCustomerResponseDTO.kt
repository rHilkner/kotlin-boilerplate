package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppCustomerDTO

class LoginAppCustomerResponseDTO(
    appCustomer: AppCustomerDTO,
    apiSession: ApiSessionResponseDTO
) : LoginResponseDTO(appCustomer, apiSession)