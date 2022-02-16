package com.example.apiboilerplate.converters

import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.AdminSignUpRequestDTO
import com.example.apiboilerplate.models.AppAdmin

class AppAdminConverter {

    fun signUpDtoToAppAdmin(adminSignUpRequestDTO: AdminSignUpRequestDTO, passwordHash: String): AppAdmin {
        return AppAdmin(adminSignUpRequestDTO, passwordHash)
    }

    fun appAdminToAppAdminDto(appAdmin: AppAdmin): AppAdminDTO {
        return AppAdminDTO(appAdmin)
    }

}