package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.AppUserDTO

abstract class LoginResponseDTO(
    val appUser: AppUserDTO,
    val apiSession: ApiSessionResponseDTO
)