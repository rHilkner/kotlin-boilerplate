package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.AdminDTO

class LoginAdminResponseDTO(
    admin: AdminDTO,
    apiSession: ApiSessionResponseDTO
) : LoginResponseDTO(admin, apiSession)