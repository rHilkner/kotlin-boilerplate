package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppAdminDTO

data class AuthAppAdminResponseDTO(
    val apiSession: ApiSessionResponseDTO,
    val appAdmin: AppAdminDTO
)