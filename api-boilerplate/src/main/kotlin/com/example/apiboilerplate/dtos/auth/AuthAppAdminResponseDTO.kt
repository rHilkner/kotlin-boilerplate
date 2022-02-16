package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppAdminDTO

data class AuthAppAdminResponseDTO(
    val apiPermissionToken: String,
    val appAdmin: AppAdminDTO
)