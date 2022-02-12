package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.models.base.ApiSession
import java.util.*

class ApiSessionResponseDTO(apiSession: ApiSession) {

    private val token: String
    private val startDt: Date
    private val lastActivityDt: Date
    private val statusCd: StatusCd

    init {
        this.token = apiSession.token
        this.startDt = apiSession.startDt
        this.lastActivityDt = apiSession.lastActivityDt
        this.statusCd = apiSession.statusCd
    }

}