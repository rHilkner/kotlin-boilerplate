package com.example.apiboilerplate.util

import com.example.apiboilerplate.dtos.AppUserDTO
import com.example.apiboilerplate.dtos.SignUpDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppUser

class AppUserCreator {

    fun createAppUserToBeSaved(): AppUser {
        return AppUser("john.silva@abcmail.com", "John Silva", "XE$%GD(A")
    }

    fun createValidAppUser(): AppUser {
        val appUser = AppUser("john.silva@abcmail.com", "John Silva", "XE$%GD(A")
        appUser.userId = 1L
        return appUser
    }

    fun createValidUpdatedAppUser(): AppUser {
        val appUser = AppUser("john.silva@abcmail.com", "Mary Smith", "XE$%GD(A")
        appUser.userId = 1L
        return appUser
    }

    fun createSignUpDto(): SignUpDTO {
        return SignUpDTO("john.silva@abcmail.com", "John Silva", "XE$%GD(A")
    }

    fun createAppUserDto(): AppUserDTO {
        return AppUserDTO(1, "John Silva", "john.silva@abcmail.com", UserRole.FREE)
    }

}