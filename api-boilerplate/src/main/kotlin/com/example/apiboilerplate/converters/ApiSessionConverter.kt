package com.example.apiboilerplate.converters

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.models.base.ApiSession

class ApiSessionConverter {

    fun apiSessionToApiSessionResponseDto(apiSession: ApiSession): ApiSessionResponseDTO {
        return ApiSessionResponseDTO(apiSession)
    }

}