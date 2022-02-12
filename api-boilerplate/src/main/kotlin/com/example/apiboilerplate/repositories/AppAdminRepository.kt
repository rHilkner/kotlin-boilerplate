package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.AppAdmin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppAdminRepository: JpaRepository<AppAdmin, Long> {

    fun getAppAdminByEmail(email: String) : AppAdmin?
    fun deleteByAdminId(adminId: Long)

}