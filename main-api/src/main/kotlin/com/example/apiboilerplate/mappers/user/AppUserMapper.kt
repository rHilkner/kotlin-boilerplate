package com.example.apiboilerplate.mappers.user

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.dtos.auth.SignUpRequestDTO
import com.example.apiboilerplate.dtos.users.AppUserDTO
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.mappers.IMapper
import com.example.apiboilerplate.models.user.AppUser
import java.util.*

class AppUserMapper: IMapper<AppUser, AppUserDTO> {

    companion object { private val log by ApiLogger() }

    override fun toDto(model: AppUser): AppUserDTO {
        return AppUserDTO(model)
    }

    fun signUpDtoToAppUser(signUpRequestDTO: SignUpRequestDTO, uuid: UUID, userRole: UserRole, passwordHash: String): AppUser {
        log.debug("Converting SignUpRequestDTO to AppUser")
        return AppUser(signUpRequestDTO, uuid, userRole, passwordHash)
    }

}