package com.example.apiboilerplate.mappers.user

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.dtos.auth.BaseLoginResponseDTO
import com.example.apiboilerplate.dtos.users.BaseUserProfileDTO
import com.example.apiboilerplate.mappers.ApiSessionMapper
import com.example.apiboilerplate.mappers.IMapper
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.models.user.AppUser
import com.example.apiboilerplate.models.user.BaseUserProfile

abstract class UserProfileMapper <Model: BaseUserProfile, Dto: BaseUserProfileDTO, LoginResponseDto: BaseLoginResponseDTO>:
    IMapper<Model, Dto> {

    private val apiSessionMapper = ApiSessionMapper()

    abstract override fun toDto(model: Model): Dto
    abstract fun createUserProfile(appUser: AppUser): Model
    abstract fun buildLoginResponse(userProfile: Dto, apiSession: ApiSessionResponseDTO): LoginResponseDto

    // Easy-to-call function to just cast UserProfile object to correct Model object
    fun buildLoginResponseOrThrow(userProfile: BaseUserProfile, apiSession: ApiSession): BaseLoginResponseDTO {
        val model = userProfile as Model
        val userProfileDto = toDto(model)
        val apiSessionDto = apiSessionMapper.toDto(apiSession)
        return buildLoginResponse(userProfileDto, apiSessionDto)
    }

}