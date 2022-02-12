package com.example.apiboilerplate.util

import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
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

    fun createSignUpDto(): AdminSignUpRequestDTO {
        return AdminSignUpRequestDTO("john.silva@abcmail.com", "John Silva", "XE$%GD(A")
    }

    fun createAppUserDto(): AppAdminDTO {
        return AppAdminDTO(1, "John Silva", "john.silva@abcmail.com", UserRole.USER)
    }

}