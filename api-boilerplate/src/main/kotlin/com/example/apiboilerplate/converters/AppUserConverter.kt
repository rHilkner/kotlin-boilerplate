package com.example.apiboilerplate.converters

import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.SignUpDTO
import com.example.apiboilerplate.models.AppUser

class AppUserConverter {

    fun signUpDtoToAppUser(signUpDTO: SignUpDTO): AppUser {
        return AppUser(signUpDTO.email, signUpDTO.name, signUpDTO.password);
    }

    fun appUserToAppUserDto(appUser: AppUser): AppUserDTO {
        return AppUserDTO(appUser.userId, appUser.email, appUser.name)
    }

}