package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.models.base.ApiSession

class ApiSessionResponseDTO(apiSession: ApiSession) {

    val token: String

    init {
        this.token = apiSession.token
    }

}