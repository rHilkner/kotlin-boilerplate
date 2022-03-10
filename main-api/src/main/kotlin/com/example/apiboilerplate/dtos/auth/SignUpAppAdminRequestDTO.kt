package com.example.apiboilerplate.dtos.auth

class SignUpAppAdminRequestDTO(
    email: String,
    name: String,
    password: String
) : SignUpRequestDTO(email, name, password)