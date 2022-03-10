package com.example.apiboilerplate.converters

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.AppAdminDTO
import com.example.apiboilerplate.dtos.auth.SignUpAppAdminRequestDTO
import com.example.apiboilerplate.models.AppAdmin

class AppAdminConverter {

    companion object { private val log by ApiLogger() }

    fun appAdminToAppAdminDto(appAdmin: AppAdmin): AppAdminDTO {
        log.debug("Converting AppAdmin to AppAdminDTO")
        return AppAdminDTO(appAdmin)
    }

    fun signUpDtoToAppAdmin(signUpAppAdminRequestDTO: SignUpAppAdminRequestDTO, passwordHash: String): AppAdmin {
        log.debug("Converting SignUpAppAdminRequestDTO to AppAdmin")
        return AppAdmin(signUpAppAdminRequestDTO, passwordHash)
    }

}