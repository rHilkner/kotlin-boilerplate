package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.IAppDTO
import com.example.apiboilerplate.dtos.users.BaseUserProfileDTO

abstract class BaseLoginResponseDTO(
    val user: BaseUserProfileDTO,
    val apiSession: ApiSessionResponseDTO
): IAppDTO