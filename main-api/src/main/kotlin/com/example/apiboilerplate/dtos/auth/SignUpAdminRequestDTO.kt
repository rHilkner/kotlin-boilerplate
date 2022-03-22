package com.example.apiboilerplate.dtos.auth

class SignUpAdminRequestDTO(
    email: String,
    name: String,
    password: String
) : SignUpRequestDTO(email, name, password)