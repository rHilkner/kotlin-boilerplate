package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.models.base.ApiSession

class ApiSessionMapper {

    fun apiSessionToApiSessionResponseDto(apiSession: ApiSession): ApiSessionResponseDTO {
        return ApiSessionResponseDTO(apiSession)
    }

}