package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.models.AppUser

data class AuthResponseDTO(
    val apiSessionToken: String,
    val appUser: AppUser
)