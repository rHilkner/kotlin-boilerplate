package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.AdminDTO

class LoginAppAdminResponseDTO(
    admin: AdminDTO,
    apiSession: ApiSessionResponseDTO
) : LoginResponseDTO(admin, apiSession)