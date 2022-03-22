package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.users.FullUserDTO

abstract class LoginResponseDTO(
    val user: FullUserDTO,
    val apiSession: ApiSessionResponseDTO
)