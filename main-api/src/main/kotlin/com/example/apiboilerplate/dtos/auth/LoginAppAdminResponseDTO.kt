package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppAdminDTO

class LoginAppAdminResponseDTO(
    appAdmin: AppAdminDTO,
    apiSession: ApiSessionResponseDTO
) : LoginResponseDTO(appAdmin, apiSession)