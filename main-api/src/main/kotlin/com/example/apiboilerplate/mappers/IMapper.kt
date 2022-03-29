package com.example.apiboilerplate.mappers

import com.example.apiboilerplate.dtos.IAppDTO
import com.example.apiboilerplate.exceptions.ApiExceptionModule

interface IMapper<Model: Any, Dto: IAppDTO> {

    fun toDto(model: Model): Dto {
        throw ApiExceptionModule.General.NotImplementedException("Not implemented `fun toDto(model: Model): Dto`")
    }

    fun toModel(dto: Dto): Model {
        throw ApiExceptionModule.General.NotImplementedException("Not implemented `fun toModel(dto: Dto): Model")
    }

}