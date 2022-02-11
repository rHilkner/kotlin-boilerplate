package com.example.apiboilerplate.converters

import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser

class AppUserConverter {

    fun signUpDtoToAppUser(signUpRequestDTO: SignUpRequestDTO, role: UserRole): AppUser {
        return AppUser(signUpRequestDTO.email, signUpRequestDTO.name, signUpRequestDTO.password, role)
    }

    fun appUserToAppUserDto(appUser: AppUser): AppUserDTO {
        return AppUserDTO(appUser.userId, appUser.email, appUser.name, appUser.role)
    }

}