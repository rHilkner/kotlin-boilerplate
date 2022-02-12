package com.example.apiboilerplate.converters

import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.AppAdmin

class AppAdminConverter {

    fun signUpDtoToAppAdmin(adminSignUpRequestDTO: AdminSignUpRequestDTO): AppAdmin {
        return AppAdmin(adminSignUpRequestDTO)
    }

    fun appAdminToAppAdminDto(appAdmin: AppAdmin): AppAdminDTO {
        return AppAdminDTO(appAdmin, UserRole.ADMIN)
    }

}