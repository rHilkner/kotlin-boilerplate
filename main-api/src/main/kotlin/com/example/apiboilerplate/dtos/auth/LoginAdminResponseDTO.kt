package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.AdminProfileDTO

class LoginAdminResponseDTO(
    admin: AdminProfileDTO,
    apiSession: ApiSessionResponseDTO
) : BaseLoginResponseDTO(admin, apiSession)