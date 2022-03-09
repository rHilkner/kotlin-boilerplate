package com.example.apiboilerplate.repositories.base

import com.example.apiboilerplate.enums.StatusCd
import com.example.apiboilerplate.models.base.ApiSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiSessionRepository: JpaRepository<ApiSession, Long> {

    fun getApiSessionByTokenAndStatusCd(token: String, statusCd: StatusCd) : ApiSession?

}