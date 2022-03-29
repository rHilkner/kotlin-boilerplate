package com.example.apiboilerplate.dtos.auth

import com.example.apiboilerplate.dtos.IAppDTO
import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.enums.UserRole
import com.example.apiboilerplate.models.base.ApiSession
import com.example.apiboilerplate.utils.ObjectUtil
import java.util.*

class ApiSessionResponseDTO(apiSession: ApiSession): IAppDTO {

    lateinit var token: String
    lateinit var startDt: Date
    lateinit var lastActivityDt: Date
    lateinit var statusCd: StatusCd
    var role: UserRole?

    init {
        ObjectUtil.copyProps(apiSession, this)
        this.role = apiSession.role
    }

}