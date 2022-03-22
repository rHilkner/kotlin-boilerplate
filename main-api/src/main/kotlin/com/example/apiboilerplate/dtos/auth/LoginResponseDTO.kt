package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.FullUserDTO

abstract class LoginResponseDTO(
    val appUser: FullUserDTO,
    val apiSession: ApiSessionResponseDTO
)