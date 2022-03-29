package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.dtos.auth.ApiSessionResponseDTO
import com.example.apiboilerplate.models.base.ApiSession

class ApiSessionMapper: IMapper<ApiSession, ApiSessionResponseDTO> {

    override fun toDto(model: ApiSession): ApiSessionResponseDTO {
        return ApiSessionResponseDTO(model)
    }

}